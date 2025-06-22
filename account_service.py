# account_service.py
from flask import Flask, json, render_template, session, redirect, url_for, request, flash
import psycopg2
from decimal import Decimal
from datetime import date

def format_currency(value):
    try:
        # Форматируем число с пробелами между тысячами и двумя знаками после запятой
        return "{:,.2f}".format(float(value)).replace(",", " ")
    except (ValueError, TypeError):
        return value


app = Flask(__name__)
app.secret_key = 'your_secret_key'  # Должно совпадать с ключом в других сервисах
app.jinja_env.filters['format_currency'] = format_currency
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
    
    # Получаем ТОЛЬКО АКТИВНЫЕ счета пользователя
    cur.execute("""
        SELECT id, account_name, currency, balance 
        FROM accounts 
        WHERE user_id = %s AND (status IS NULL OR status = 'active')
        ORDER BY created_at DESC
    """, (session['user_id'],))
    accounts = cur.fetchall()
    
    # Получаем общий баланс ТОЛЬКО ПО АКТИВНЫМ СЧЕТАМ
    cur.execute("""
        SELECT currency, SUM(balance) as total 
        FROM accounts 
        WHERE user_id = %s AND (status IS NULL OR status = 'active')
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
    
    # Конвертация в рубли для общего баланса
    EXCHANGE_RATES = {
        'USD': 90.5, 'EUR': 98.7, 'RUB': 1,
        'GBP': 114.2, 'JPY': 0.61, 'CNY': 12.5, 'CHF': 102.3
    }
    
    total_balance_rub = 0
    for currency, amount in totals:
        rate = EXCHANGE_RATES.get(currency, 1)
        total_balance_rub += float(amount) * rate

    return render_template(
        'accounts.html',
        first_name=first_name,
        last_name=last_name,
        accounts=accounts_list,
        totals=totals,
        currencies=currencies,
        total_balance_rub=total_balance_rub
    )

# Маршрут для создания нового счета
@app.route('/accounts/create', methods=['POST'])
def create_account():
    if 'user_id' not in session:
        return redirect('http://localhost:5000/login')
    account_name = request.form['account_name']
    currency = request.form['currency']
    initial_balance = request.form.get('initial_balance', 0) or 0
    source = request.form.get('source', 'main')  # Получаем источник запроса
    
    try:
        initial_balance = Decimal(initial_balance)
    except:
        flash('Некорректная сумма начального баланса', 'danger')
        return redirect(url_for('accounts' if source == 'main' else 'all_accounts'))
    
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
    
    # Редирект в зависимости от источника
    if source == 'all':
        return redirect(url_for('all_accounts'))
    else:
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
            # Удаляем связанные транзакции
            cur.execute("DELETE FROM transactions WHERE account_id = %s", (account_id,))
            
            # Удаляем сам счет
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



# Маршрут для удаления счета
@app.route('/accounts/all/delete/<int:account_id>', methods=['POST'])
def delete_from_all_account(account_id):
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
            # Удаляем связанные транзакции
            cur.execute("DELETE FROM transactions WHERE account_id = %s", (account_id,))
            
            # Удаляем сам счет
            cur.execute("DELETE FROM accounts WHERE id = %s", (account_id,))
            
            conn.commit()
            flash('Счет успешно удален', 'success')
    except Exception as e:
        conn.rollback()
        flash(f'Ошибка при удалении счета: {str(e)}', 'danger')
    finally:
        cur.close()
        conn.close()
    
    return redirect(url_for('all_accounts'))


# Изменяем функцию удаления счета (теперь меняем статус вместо удаления)
@app.route('/accounts/close/<int:account_id>', methods=['POST'])
def close_account(account_id):
    if 'user_id' not in session:
        return redirect('http://localhost:5000/login')
    
    conn = get_db_connection()
    cur = conn.cursor()
    
    try:
        cur.execute("UPDATE accounts SET status = 'closed' WHERE id = %s AND user_id = %s", 
                    (account_id, session['user_id']))
        conn.commit()
        flash('Счет успешно закрыт', 'success')
    except Exception as e:
        conn.rollback()
        flash(f'Ошибка при закрытии счета: {str(e)}', 'danger')
    finally:
        cur.close()
        conn.close()
    
    return redirect(url_for('all_accounts'))

# Добавляем функцию восстановления счета
@app.route('/accounts/restore/<int:account_id>', methods=['POST'])
def restore_account(account_id):
    if 'user_id' not in session:
        return redirect('http://localhost:5000/login')
    
    conn = get_db_connection()
    cur = conn.cursor()
    
    try:
        cur.execute("UPDATE accounts SET status = 'active' WHERE id = %s AND user_id = %s", 
                    (account_id, session['user_id']))
        conn.commit()
        flash('Счет успешно восстановлен', 'success')
    except Exception as e:
        conn.rollback()
        flash(f'Ошибка при восстановлении счета: {str(e)}', 'danger')
    finally:
        cur.close()
        conn.close()
    
    return redirect(url_for('all_accounts'))
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


# Добавляем новую функцию для отображения всех счетов
@app.route('/accounts/all')
def all_accounts():
    if 'user_id' not in session:
        return redirect('http://localhost:5000/login')
    
    conn = get_db_connection()
    cur = conn.cursor()
    
    # Получаем данные пользователя
    cur.execute("SELECT first_name, last_name FROM users WHERE id = %s", (session['user_id'],))
    user_data = cur.fetchone()
    
    if not user_data:
        cur.close()
        conn.close()
        return redirect('http://localhost:5000/login')
    
    first_name, last_name = user_data
    
    # Получаем ВСЕ счета пользователя (включая закрытые)
    cur.execute("""
        SELECT id, account_name, currency, balance, status, created_at 
        FROM accounts 
        WHERE user_id = %s
        ORDER BY 
            CASE WHEN status = 'active' THEN 1 ELSE 2 END,
            created_at DESC
    """, (session['user_id'],))
    accounts = cur.fetchall()
    
    # Получаем список доступных валют
    cur.execute("SELECT code, name, symbol FROM currencies ORDER BY name")
    currencies = cur.fetchall()
    
    cur.close()
    conn.close()
    
    accounts_list = []
    for acc in accounts:
        # Форматируем дату создания
        created_at = acc[5].strftime("%d.%m.%Y %H:%M")
        accounts_list.append({
            'id': acc[0],
            'name': acc[1],
            'currency': acc[2],
            'balance': acc[3],
            'status': acc[4],
            'created_at': created_at  # Добавляем отформатированную дату
        })
    
    return render_template(
        'all_accounts.html',  # Новый шаблон
        first_name=first_name,
        last_name=last_name,
        accounts=accounts_list,
        currencies=currencies
    )


# account_service.py

# Маршрут для страницы целей
@app.route('/accounts/goals')
def financial_goals():
    if 'user_id' not in session:
        return redirect('http://localhost:5000/login')
    
    conn = get_db_connection()
    cur = conn.cursor()
    
    # Получаем цели пользователя
    cur.execute("""
        SELECT g.id, g.name, g.target_amount, g.current_amount, g.deadline, 
               g.status, a.account_name, g.account_id
        FROM goals g
        LEFT JOIN accounts a ON g.account_id = a.id
        WHERE g.user_id = %s
        ORDER BY g.deadline ASC
    """, (session['user_id'],))
    goals = cur.fetchall()
    
    # Получаем счета для формы
    cur.execute("""
        SELECT id, account_name, currency 
        FROM accounts 
        WHERE user_id = %s AND status = 'active'
    """, (session['user_id'],))
    accounts = cur.fetchall()
    
    cur.close()
    conn.close()
    
    goals_list = []
    for goal in goals:
        # Рассчитываем прогресс и оставшиеся дни
        progress = (goal[3] / goal[2]) * 100 if goal[2] > 0 else 0
        deadline = datetime.strptime(str(goal[4]), '%Y-%m-%d')
        days_left = (deadline - datetime.now()).days
        
        goals_list.append({
            'id': goal[0],
            'name': goal[1],
            'target': goal[2],
            'current': goal[3],
            'deadline': goal[4].strftime("%d.%m.%Y"),
            'status': goal[5],
            'account_name': goal[6],
            'account_id': goal[7],
            'progress': min(100, round(progress, 1)),
            'days_left': max(0, days_left)
        })
    
    current_date = date.today().strftime('%Y-%m-%d')
    
    return render_template(
        'goals.html',
        goals=goals_list,
        accounts=accounts,
        current_date=current_date  # Передаем в шаблон
    )

# Создание новой цели
@app.route('/goals/create', methods=['POST'])
def create_goal():
    if 'user_id' not in session:
        return redirect('http://localhost:5000/login')
    
    name = request.form['name']
    target = request.form['target']
    deadline = request.form['deadline']
    account_id = request.form.get('account_id', None)
    
    try:
        target = Decimal(target)
        if target <= 0:
            raise ValueError("Цель должна быть больше 0")
            
        deadline = datetime.strptime(deadline, '%Y-%m-%d').date()
        if deadline < date.today():
            flash('Дата окончания не может быть в прошлом', 'danger')
            return redirect(url_for('financial_goals'))
            
    except Exception as e:
        flash(f'Ошибка в данных: {str(e)}', 'danger')
        return redirect(url_for('financial_goals'))
    
    conn = get_db_connection()
    cur = conn.cursor()
    
    try:
        cur.execute("""
            INSERT INTO goals (user_id, name, target_amount, deadline, account_id)
            VALUES (%s, %s, %s, %s, %s)
        """, (session['user_id'], name, target, deadline, account_id))
        
        conn.commit()
        flash('Цель успешно создана', 'success')
    except Exception as e:
        conn.rollback()
        flash(f'Ошибка при создании цели: {str(e)}', 'danger')
    finally:
        cur.close()
        conn.close()
    
    return redirect(url_for('financial_goals'))

# Удаление цели
@app.route('/goals/delete/<int:goal_id>', methods=['POST'])
def delete_goal(goal_id):
    if 'user_id' not in session:
        return redirect('http://localhost:5000/login')
    
    conn = get_db_connection()
    cur = conn.cursor()
    
    try:
        cur.execute("""
            DELETE FROM goals 
            WHERE id = %s AND user_id = %s
        """, (goal_id, session['user_id']))
        
        conn.commit()
        flash('Цель успешно удалена', 'success')
    except Exception as e:
        conn.rollback()
        flash(f'Ошибка при удалении цели: {str(e)}', 'danger')
    finally:
        cur.close()
        conn.close()
    
    return redirect(url_for('financial_goals'))

# Добавление средств к цели
@app.route('/goals/add_funds/<int:goal_id>', methods=['POST'])
def add_funds_to_goal(goal_id):
    if 'user_id' not in session:
        return redirect('http://localhost:5000/login')
    
    amount = request.form['amount']
    
    try:
        amount = Decimal(amount)
        if amount <= 0:
            raise ValueError("Сумма должна быть больше 0")
    except:
        flash('Некорректная сумма', 'danger')
        return redirect(url_for('financial_goals'))
    
    conn = get_db_connection()
    cur = conn.cursor()
    
    try:
        # Проверяем существование цели
        cur.execute("""
            SELECT target_amount, current_amount, account_id
            FROM goals
            WHERE id = %s AND user_id = %s
        """, (goal_id, session['user_id']))
        goal = cur.fetchone()
        
        if not goal:
            flash('Цель не найдена', 'danger')
            return redirect(url_for('financial_goals'))
        
        target, current, account_id = goal
        new_amount = current + amount
        
        # Обновляем цель
        cur.execute("""
            UPDATE goals 
            SET current_amount = %s 
            WHERE id = %s
        """, (new_amount, goal_id))
        
        # Если цель привязана к счету, обновляем баланс счета
        if account_id:
            cur.execute("""
                UPDATE accounts 
                SET balance = balance - %s 
                WHERE id = %s
            """, (amount, account_id))
            
            # Записываем транзакцию
            cur.execute("""
                INSERT INTO transactions (account_id, amount, transaction_type, description)
                VALUES (%s, %s, 'goal', %s)
            """, (account_id, amount, f"Пополнение цели: {request.form['name']}"))
        
        conn.commit()
        flash('Средства успешно добавлены к цели', 'success')
    except Exception as e:
        conn.rollback()
        flash(f'Ошибка: {str(e)}', 'danger')
    finally:
        cur.close()
        conn.close()
    
    return redirect(url_for('financial_goals'))


if __name__ == '__main__':
    app.run(debug=True, port=5002)