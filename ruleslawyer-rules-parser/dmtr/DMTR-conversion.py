import sys
sys.path.append("..")
from utils.csv_to_json import csv_to_json
from utils.simple_io import write
from utils.simple_io import clear
from utils.simple_io import read_csv


def main():
    doc_raw = read_csv("DMTR-parsed.csv")
    clear("DMTR-parsed.json")
    write("DMTR-parsed.json", csv_to_json(doc_raw))


if __name__ == "__main__":
    main()
