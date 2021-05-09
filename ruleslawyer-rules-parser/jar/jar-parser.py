import sys
sys.path.append("..")
from simple_io import getPDF
from simple_io import write
from simple_io import clear
from filedata import FileData
from contract.rules import RuleHeader
from contract.rules import RuleSubHeader


def main():
    file = FileData(getPDF("JAR.pdf"))
    normalized_text = []
    line_builder = ""
    emptyline_flag = True
    while file.has_line():
        line = file.next_line().replace("  ", " ").replace("—", "-").replace("•", "*").replace("’", "'").replace("™", "")
        if len(line) == 0:
            emptyline_flag = True
            continue
        else:
            emptyline_flag = False

        is_new_line = (line[0].isupper() or not line[0].isalpha()) and (emptyline_flag or (len(line_builder) == 0 or not line_builder[-2].isalpha()) or len(line_builder) < 30)

        if is_new_line and len(line_builder) != 0:
            normalized_text.append(line_builder)
            line_builder = ""
        line_builder = line_builder + line
        if line.startswith("All trademarks are property of Wizards of the Coast"):
            break
    normalized_text.append(line_builder)

    rules = []
    last_header = RuleHeader("Introduction", [])
    for line in normalized_text:
        if len(line) < 30 and not line[0].startswith("Updated"):
            rules.append(last_header)
            last_header = RuleHeader(line, [])
        else:
            last_header.subrules.append(RuleSubHeader(line, []))

    rules.append(last_header)

    clear("JAR-parsed.json")
    write("JAR-parsed.json", rules)


if __name__ == "__main__":
    main()