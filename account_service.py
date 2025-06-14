# account_service.py
from flask import Flask, render_template, session, redirect, url_for, request, flash
import psycopg2
from decimal import Decimal

app = Flask(__name__)
app.secret_key = 'your_secret_key'  # Должно совпадать с ключом в других сервисах

# Подключение к базе данных (такое же как в других сервисах)
def get_db_connection():
    conn = psycopg2.connect(
        host="localhost",
        database="fintime",
        user="postgres",
        password="postgres"
    )
    return conn

@app.route('/accounts')
def accounts():
    if 'user_id' not in session:
        return redirect('http://localhost:5000/login')
    
    conn = get_db_connection()
    cur = conn.cursor()
    
    # Получаем данные пользователя
    cur.execute("""
        SELECT first_name, last_name 
        FROM users 
        WHERE id = %s
    """, (session['user_id'],))
    user_data = cur.fetchone()
    
    # Проверяем, что данные пользователя получены
    if not user_data:
        cur.close()
        conn.close()
        return redirect('http://localhost:5000/login')
    
    first_name, last_name = user_data
    
    # Получаем счета пользователя
    cur.execute("""
        SELECT id, account_name, currency, balance 
        FROM accounts 
        WHERE user_id = %s
        ORDER BY created_at DESC
    """, (session['user_id'],))
    accounts = cur.fetchall()
    
    # Получаем общий баланс по всем счетам
    cur.execute("""
        SELECT currency, SUM(balance) as total 
        FROM accounts 
        WHERE user_id = %s 
        GROUP BY currency
    """, (session['user_id'],))
    totals = cur.fetchall()
    
    # Получаем список доступных валют
    cur.execute("SELECT code, name, symbol FROM currencies ORDER BY name")
    currencies = cur.fetchall()
    
    cur.close()
    conn.close()
    
    # Преобразуем данные счетов в список словарей
    accounts_list = []
    for acc in accounts:
        accounts_list.append({
            'id': acc[0],
            'name': acc[1],
            'currency': acc[2],
            'balance': acc[3]
        })
    
    return render_template(
        'accounts.html',
        first_name=first_name,
        last_name=last_name,
        accounts=accounts_list,
        totals=totals,
        currencies=currencies
    )

# Маршрут для создания нового счета
@app.route('/accounts/create', methods=['POST'])
def create_account():
    if 'user_id' not in session:
        return redirect('http://localhost:5000/login')
    
    account_name = request.form['account_name']
    currency = request.form['currency']
    initial_balance = request.form.get('initial_balance', 0) or 0
    
    try:
        initial_balance = Decimal(initial_balance)
    except:
        flash('Некорректная сумма начального баланса', 'danger')
        return redirect(url_for('accounts'))
    
    conn = get_db_connection()
    cur = conn.cursor()
    
    try:
        cur.execute("""
            INSERT INTO accounts (user_id, account_name, currency, balance)
            VALUES (%s, %s, %s, %s)
        """, (session['user_id'], account_name, currency, initial_balance))
        conn.commit()
        flash('Счет успешно создан', 'success')
    except Exception as e:
        conn.rollback()
        flash(f'Ошибка при создании счета: {str(e)}', 'danger')
    finally:
        cur.close()
        conn.close()
    
    return redirect(url_for('accounts'))

# Маршрут для удаления счета
@app.route('/accounts/delete/<int:account_id>', methods=['POST'])
def delete_account(account_id):
    if 'user_id' not in session:
        return redirect('http://localhost:5000/login')
    
    conn = get_db_connection()
    cur = conn.cursor()
    
    try:
        # Проверяем, принадлежит ли счет пользователю
        cur.execute("SELECT user_id FROM accounts WHERE id = %s", (account_id,))
        account = cur.fetchone()
        
        if not account or account[0] != session['user_id']:
            flash('Счет не найден или у вас нет прав для его удаления', 'danger')
        else:
            cur.execute("DELETE FROM accounts WHERE id = %s", (account_id,))
            conn.commit()
            flash('Счет успешно удален', 'success')
    except Exception as e:
        conn.rollback()
        flash(f'Ошибка при удалении счета: {str(e)}', 'danger')
    finally:
        cur.close()
        conn.close()
    
    return redirect(url_for('accounts'))

# Маршрут для пополнения счета
@app.route('/accounts/deposit/<int:account_id>', methods=['POST'])
def deposit_account(account_id):
    return _update_balance(account_id, 'deposit')

# Маршрут для снятия средств
@app.route('/accounts/withdraw/<int:account_id>', methods=['POST'])
def withdraw_account(account_id):
    return _update_balance(account_id, 'withdraw')

# Общая функция для обновления баланса
def _update_balance(account_id, operation_type):
    if 'user_id' not in session:
        return redirect('http://localhost:5000/login')
    
    amount = request.form['amount']
    
    try:
        amount = Decimal(amount)
        if amount <= 0:
            raise ValueError("Amount must be positive")
    except:
        flash('Некорректная сумма', 'danger')
        return redirect(url_for('accounts'))
    
    conn = get_db_connection()
    cur = conn.cursor()
    
    try:
        # Проверяем принадлежность счета
        cur.execute("""
            SELECT id, balance, currency 
            FROM accounts 
            WHERE id = %s AND user_id = %s
        """, (account_id, session['user_id']))
        account = cur.fetchone()
        
        if not account:
            flash('Счет не найден', 'danger')
            return redirect(url_for('accounts'))
        
        current_balance = account[1]
        
        if operation_type == 'deposit':
            new_balance = current_balance + amount
        else:  # withdraw
            if current_balance < amount:
                flash('Недостаточно средств на счете', 'danger')
                return redirect(url_for('accounts'))
            new_balance = current_balance - amount
        
        # Обновляем баланс
        cur.execute("""
            UPDATE accounts 
            SET balance = %s 
            WHERE id = %s
        """, (new_balance, account_id))
        
        # Записываем транзакцию
        transaction_type = 'deposit' if operation_type == 'deposit' else 'withdrawal'
        cur.execute("""
            INSERT INTO transactions (account_id, amount, transaction_type, description)
            VALUES (%s, %s, %s, %s)
        """, (account_id, amount, transaction_type, f"Операция: {transaction_type}"))
        
        conn.commit()
        flash('Операция выполнена успешно', 'success')
    except Exception as e:
        conn.rollback()
        flash(f'Ошибка при выполнении операции: {str(e)}', 'danger')
    finally:
        cur.close()
        conn.close()
    
    return redirect(url_for('accounts'))

if __name__ == '__main__':
    app.run(debug=True, port=5002)