import os

def read_file_content(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8', errors='replace') as file:
            return file.read()
    except Exception as e:
        return f"!!! Ошибка при чтении файла: {str(e)}"

def process_directory(directory, report):
    for entry in os.listdir(directory):
        full_path = os.path.join(directory, entry)
        
        if os.path.isdir(full_path):
            process_directory(full_path, report)
        elif os.path.isfile(full_path):
            report.write(f"\n{'*' * 50}\n")
            report.write(f"**** Имя файла: {full_path} *****\n")
            report.write(f"{'*' * 50}\n\n")
            report.write(read_file_content(full_path))
            report.write("\n\n")

with open('report.txt', 'w', encoding='utf-8') as report_file:
    report_file.write(f"{'*' * 50}\n")
    report_file.write("**** ОТЧЕТ ПО ФАЙЛАМ В ДИРЕКТОРИИ *****\n")
    report_file.write(f"{'*' * 50}\n\n")
    process_directory('.', report_file)