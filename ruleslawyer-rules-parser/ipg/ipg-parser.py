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


def main():
    file = FileData(get_PDF("IPG.pdf"))

    normalized_text = []
    line_builder = ""
    emptyline_flag = True

    # parse into blocks
    while file.has_line():
        line = replace_unprintable(file.next_line())
        if len(line) < 4:
            emptyline_flag = True
            continue

        is_new_line = (line[0].isupper() or not line[0].isalpha()) and (emptyline_flag or (len(line_builder) == 0 or not line_builder[-1].isalpha()) or len(line_builder) < 75)

        if is_new_line and len(line_builder) != 0:
            normalized_text.append(line_builder)
            line_builder = ""
        line_builder = line if len(line_builder) == 0 else line_builder + " " + line
        emptyline_flag = False
    normalized_text.append(line_builder)

    start_index = normalized_text.index("1. GENERAL PHILOSOPHY")
    output = []
    current_header = None
    current_subheader = None
    penalties = ["No Penalty", "Warning", "Game Loss", "Match Loss", "Disqualification"]
    for i in range(start_index, len(normalized_text)):
        line = normalized_text[i].replace("  ", " ").strip()
        if line.startswith("APPENDIX A"):
            break
        if line[0].isnumeric() and line[1] == ".":  # is header
            if current_header:
                if current_subheader:
                    current_header.subrules.append(current_subheader)
                    current_subheader = None
                output.append(current_header)

            flag = False
            for penalty in penalties:
                ind = line.find(penalty)
                if ind != -1:
                    try:
                        citation_number_index = line[:ind].index(" ")
                        citation_name_index = line[:ind].index("-")
                        current_header = RuleHeader(line[:ind], [], ["IPG " + line[:citation_number_index], "IPG " + line[citation_name_index+1:ind].strip()])
                    except ValueError:
                        current_header = RuleHeader(line[:ind], [], [])
                    current_header.subrules.append(RuleSubHeader("Penalty", [Rule(penalty, [])], []))
                    flag = True
                    break
            if not flag:
                current_header = RuleHeader(line, [], [])
            current_subheader = None
            continue

        if not current_subheader:  # if just made a header
            if len(line) > 30:  # is not a section header
                current_header.subrules.append(RuleSubHeader(line, [], []))
            else:
                current_subheader = RuleSubHeader(line, [], [])
        else:
            if len(line) < 30:  # is a section header
                current_header.subrules.append(current_subheader)
                current_subheader = RuleSubHeader(line, [], [])
            else:
                current_subheader.subrules.append(Rule(line, []))
    current_header.subrules.append(current_subheader)
    output.append(current_header)

    csv_output = []
    for rule in output:
        csv_output = csv_output + rule.toArray()

    clear("IPG-parsed.csv")
    write_csv("IPG-parsed.csv", csv_output)

    # TODO maybe the appendix too?


if __name__ == "__main__":
    main()