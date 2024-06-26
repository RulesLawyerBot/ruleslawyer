import sys
sys.path.append("..")
from utils.csv_to_json import csv_to_json
from utils.simple_io import write
from utils.simple_io import clear
from utils.simple_io import read_csv

def main():
    doc_raw = read_csv("JAR-parsed.csv")
    clear("JAR-parsed.json")
    write("JAR-parsed.json", csv_to_json(doc_raw))


if __name__ == "__main__":
    main()
