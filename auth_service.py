from flask import Flask, render_template, request, redirect, url_for, session
import psycopg2
import hashlib

app = Flask(__name__)
app.secret_key = 'your_secret_key'  # Замените на безопасный ключ

# Подключение к базе данных
def get_db_connection():
    conn = psycopg2.connect(
        host="localhost",
        database="fintime",
        user="postgres",  # Замените на ваше имя пользователя PostgreSQL
        password="postgres"  # Замените на ваш пароль PostgreSQL
    )
    return conn

# Хеширование пароля
def hash_password(password):
    return hashlib.sha256(password.encode()).hexdigest()

# Маршрут для входа
@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        email = request.form['email']
        password = hash_password(request.form['password'])
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute("SELECT * FROM users WHERE email = %s AND password = %s", (email, password))
        user = cur.fetchone()
        cur.close()
        conn.close()
        if user:
            session['user_id'] = user[0]
            return redirect('http://localhost:5001/profile')
        else:
            return "Неверные учетные данные"
    return render_template('login.html')

# Маршрут для регистрации
@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        first_name = request.form['first_name']
        last_name = request.form['last_name']
        email = request.form['email']
        password = hash_password(request.form['password'])
        conn = get_db_connection()
        cur = conn.cursor()
        try:
            cur.execute("INSERT INTO users (first_name, last_name, email, password) VALUES (%s, %s, %s, %s)",
                        (first_name, last_name, email, password))
            conn.commit()
            cur.close()
            conn.close()
            return redirect(url_for('login'))
        except psycopg2.IntegrityError:
            conn.rollback()
            cur.close()
            conn.close()
            return "Email уже существует"
    return render_template('register.html')

# Маршрут для выхода
@app.route('/logout')
def logout():
    session.pop('user_id', None)
    return redirect(url_for('login'))

if __name__ == '__main__':
    app.run(debug=True, port=5000)