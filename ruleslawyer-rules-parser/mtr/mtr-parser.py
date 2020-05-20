import sys
sys.path.append("..")
from simple_io import getPDF
from simple_io import write
from simple_io import clear
from filedata import FileData
from contract.rules import RuleHeader
from contract.rules import RuleSubHeader
from contract.rules import Rule


def main():
    file = FileData(getPDF("MTR.pdf"))
    normalized_text = []
    line_builder = ""
    emptyline_flag = True

    # parse into blocks
    while file.has_line():
        line = file.next_line().replace("  ", " ").replace("—", "-").replace("•", "*").replace("’", "'")
        if len(line) < 4:
            emptyline_flag = True
            continue

        is_new_line = (line[0].isupper() or not line[0].isalpha()) and (emptyline_flag or (len(line_builder) == 0 or not line_builder[-2].isalpha()) or len(line_builder) < 75)

        if is_new_line and len(line_builder) != 0:
            normalized_text.append(line_builder)
            line_builder = ""
        line_builder = line_builder + line
        emptyline_flag = False
    normalized_text.append(line_builder)

    for line in normalized_text:
        print(line)

    start_index = normalized_text.index("Introduction ")
    rules = []
    current_header = RuleHeader("Introduction", [])
    current_subheader = None
    for i in range(start_index, len(normalized_text)):
        line = normalized_text[i]
        if line.startswith("Appendix"):
            break

        if (line[0].isnumeric() and line[1] == ".") or (line[0:1].isnumeric() and line[2] == "."):  # header
            if current_header:
                rules.append(current_header)
            current_header = RuleHeader(line, [])
        elif line[0].isnumeric() or line[0] == "*":  # subsection
            current_subheader.subrules.append(Rule(line))
        else:  # subheader
            if current_subheader:
                current_header.subrules.append(current_subheader)
            current_subheader = RuleSubHeader(line, [])
    rules.append(current_header)

    clear("MTR-parsed.json")
    write("MTR-parsed.json", rules)



if __name__ == "__main__":
    main()