from flask import Flask, render_template, session, redirect, url_for, request, flash
import psycopg2

app = Flask(__name__)
app.secret_key = 'your_secret_key'  

# Подключение к базе данных
def get_db_connection():
    conn = psycopg2.connect(
        host="localhost",
        database="fintime",
        user="postgres",  
        password="postgres"  
    )
    return conn


@app.route('/profile')
def profile():
    if 'user_id' not in session:
        return redirect('http://localhost:5000/login')
    conn = get_db_connection()
    cur = conn.cursor()
    cur.execute("SELECT first_name, last_name, email FROM users WHERE id = %s", (session['user_id'],))
    user = cur.fetchone()
    cur.close()
    conn.close()
    if user:
        return render_template('profile.html', 
                               first_name=user[0], 
                               last_name=user[1], 
                               email=user[2])
    else:
        return "Пользователь не найден"


@app.route('/profile/update', methods=['POST'])
def update_profile():
    if 'user_id' not in session:
        return redirect(url_for('login'))
    
    
    first_name = request.form['first_name'].strip()
    last_name = request.form['last_name'].strip()
    email = request.form['email'].strip().lower()
    
    
    errors = []
    if not first_name:
        errors.append("Имя не может быть пустым")
    if not last_name:
        errors.append("Фамилия не может быть пустой")
    if not email:
        errors.append("Email не может быть пустым")
    elif '@' not in email:
        errors.append("Неверный формат email")
    
    if errors:
        for error in errors:
            flash(error, 'danger')
        return redirect('/profile')
    
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        
        
        cur.execute("SELECT id FROM users WHERE email = %s AND id != %s", (email, session['user_id']))
        if cur.fetchone():
            flash("Пользователь с таким email уже существует", 'danger')
            return redirect('/profile')
        
        
        cur.execute("""
            UPDATE users 
            SET first_name = %s, last_name = %s, email = %s
            WHERE id = %s
        """, (first_name, last_name, email, session['user_id']))
        
        conn.commit()
        flash("Данные профиля успешно обновлены", 'success')
        
        cur.close()
        conn.close()
        
        return redirect('/profile')
        
    except Exception as e:
        flash(f"Ошибка при обновлении профиля: {str(e)}", 'danger')
        return redirect('/profile')

if __name__ == '__main__':
    app.run(debug=True, port=5001)