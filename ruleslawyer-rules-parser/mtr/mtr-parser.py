import sys
sys.path.append("..")
from utils.simple_io import get_PDF
from utils.simple_io import clear
from utils.simple_io import write_csv
from utils.filedata import FileData
from utils.unprintable_remover import replace_unprintable
from contract.rules import RuleHeader
from contract.rules import RuleSubHeader
from contract.rules import Rule

LINE_ENDINGS = [".", ")", '"']


def main():
    file = FileData(get_PDF("MTR.pdf"))
    normalized_text = []
    line_builder = ""
    emptyline_flag = True

    # parse into blocks
    while file.has_line():
        line = replace_unprintable(file.next_line())
        if len(line) < 4:
            emptyline_flag = True
            continue

        is_new_line = (line[0].isupper() or not line[0].isalpha()) and (emptyline_flag or (len(line_builder) == 0 or line_builder[-1] in LINE_ENDINGS) or len(line_builder) < 75) and (line_builder.find(")") > -1 or line_builder.find("(") == -1)

        if is_new_line and len(line_builder) != 0:
            normalized_text.append(line_builder)
            line_builder = ""
        line_builder = line if len(line_builder) == 0 else line_builder + " " + line
        emptyline_flag = False
    normalized_text.append(line_builder)

    for line in normalized_text:
        print(line)

    start_index = normalized_text.index("Introduction")
    rules = []
    current_header = RuleHeader("Introduction", [], [])
    current_subheader = None
    for i in range(start_index, len(normalized_text)):
        line = normalized_text[i]
        if line.startswith("Appendix"):
            break

        if verify_header(current_header, line):  # header
            print(line)
            if current_subheader:
                current_header.subrules.append(current_subheader)
                current_subheader = None
            if current_header:
                rules.append(current_header)
            try:
                citation_split_index = line.index(" ")
                current_header = RuleHeader(line, [], ["MTR " + line[:citation_split_index].strip(), "MTR " + line[citation_split_index:].strip()])
            except ValueError:
                current_header = RuleHeader(line, [], [])
        elif line[0].isnumeric() or line[0] == "*":  # subsection
            if not current_subheader:
                current_subheader = RuleSubHeader(line, [], [])
            current_subheader.subrules.append(Rule(line, []))
        else:  # subheader
            if current_subheader:
                current_header.subrules.append(current_subheader)
            current_subheader = RuleSubHeader(line, [], [])
    rules.append(current_header)

    csv_output = []
    for rule in rules:
        csv_output = csv_output + rule.toArray()

    clear("MTR-parsed.csv")
    write_csv("MTR-parsed.csv", csv_output)


def verify_header(previous_header, line):
    if not line[-1].isalpha():
        return False
    if not (line[0].isnumeric() and line[1] == "." or (line[0:1].isnumeric() and line[2] == ".")):
        return False
    if not all([(not x[0].isalpha() or x[0].isupper()) for x in line.split(" ")]):
        return False
    if previous_header:
        try:
            return float(previous_header.text.split(" ")[0][:-1]) <= float(line.split(" ")[0][:-1])
        except ValueError:
            return True
    else:
        return False
    return False


if __name__ == "__main__":
    main()
