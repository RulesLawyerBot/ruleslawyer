import sys
sys.path.append("..")
from simple_io import open_file
from simple_io import write
from simple_io import clear
from contract.rules import RuleSuperHeader
from contract.rules import RuleHeader
from contract.rules import RuleSubHeader
from contract.rules import Rule


def main():
    raw_lines = open_file("oathbreaker.txt")
    output = RuleSuperHeader("906. Oathbreaker", [])
    current_header = None
    for line in raw_lines:
        print(line)
        if not line:
            continue
        if (line.split(" ")[0][-1]).isalpha():  # is base rule
            current_header.subrules[0].subrules.append(Rule(line))
        else:
            if current_header:
                output.subrules.append(current_header)
            citation = line.split(" ")[0]
            rule_text = " ".join(line.split(" ")[1:])
            current_header = RuleHeader(citation, [RuleSubHeader(rule_text, [])])

    output.subrules.append(current_header)
    clear("oath-parsed.json")
    write("oath-parsed.json", [output])


if __name__ == "__main__":
    main()