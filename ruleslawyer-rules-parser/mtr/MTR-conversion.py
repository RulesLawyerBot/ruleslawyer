import sys
sys.path.append("..")
from utils.csv_to_json import csv_to_json
from utils.simple_io import write
from utils.simple_io import clear
from utils.simple_io import read_csv

def main():
    cr_raw = read_csv("MTR-parsed.csv")
    clear("MTR-parsed.json")
    write("MTR-parsed.json", csv_to_json(cr_raw))


if __name__ == "__main__":
    main()
