import sys
sys.path.append("..")
from utils.simple_io import getPDF
from utils.simple_io import write
from utils.simple_io import clear
from utils.filedata import FileData
from utils.unprintable_remover import replace_unprintable
from contract.rules import RuleHeader
from contract.rules import RuleSubHeader
from contract.rules import Rule


NONSENSE_LINKS = ["https://magic.wizards.com/en/mtgarena/faq", "mailto:esports@wizards.com", "https://company.wizards.com/legal/terms", "https://company.wizards.com/legal/wizards-coasts-privacy-policy", "https://company.wizards.com/legal/code-conduct@wizards.com"]
# seriously, what the fuck


def main():
    file = FileData(getPDF("dmtr.pdf"))
    normalized_text = []
    line_builder = ""
    emptyline_flag = True

    # parse into lines
    while file.has_line():
        line = replace_unprintable(file.next_line())
        for elem in NONSENSE_LINKS:
            line = line.replace(elem, "")
        print(line)
        if len(line) < 2:
            continue
        is_new_line = (line[0].isupper() or not line[0].isalpha()) and (emptyline_flag or (len(line_builder) == 0 or not line_builder[-1].isalpha()) or len(line_builder) < 75) or (len(line) > 3 and not line[0].isalpha() and line[1] == ".")

        if is_new_line and len(line_builder) != 0:
            normalized_text.append(line_builder)
            line_builder = ""
        line_builder = line if len(line_builder) == 0 else line_builder + " " + line
        emptyline_flag = False

    for line in normalized_text:
        print(line)

    rules = []
    current_header = RuleHeader("Introduction", [])
    current_subheader = None
    for i in range(0, len(normalized_text)):
        line = normalized_text[i]
        if line.startswith("Appendix"):
            break

        if ((line[0].isnumeric() and line[1] == ".") or (line[0:1].isnumeric() and line[2] == ".")) and line[-1].isalpha():  # header
            if current_subheader:
                current_header.subrules.append(current_subheader)
                current_subheader = None
            if current_header:
                rules.append(current_header)
            current_header = RuleHeader(line, [])
        elif line[0].isnumeric() or line[0] == "*":  # subsection
            if not current_subheader:
                current_subheader = RuleSubHeader(line, [])
            current_subheader.subrules.append(Rule(line))
        else:  # subheader
            if current_subheader:
                current_header.subrules.append(current_subheader)
            current_subheader = RuleSubHeader(line, [])
    current_header.subrules.append(current_subheader)
    rules.append(current_header)

    clear("DMTR-parsed.json")
    write("DMTR-parsed.json", rules)


if __name__ == "__main__":
    main()