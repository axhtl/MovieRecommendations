from app import create_app

app = create_app()

if __name__ == '__main__':
    app.run(debug=True, host='127.0.0.1', port=5000)  # 5000번 포트에서 실행