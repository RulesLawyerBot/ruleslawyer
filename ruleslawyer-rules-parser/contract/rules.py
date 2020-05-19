class RuleSuperHeader:
    def __init__(self, text, subrules):
        self.text = text.replace('"', '\\"')
        self.subrules = subrules

    def __repr__(self):
        return '{"text":"' + self.text + '","subRules":' + str(self.subrules) + '}'


class RuleHeader:
    def __init__(self, text, subrules):
        self.text = text.replace('"', '\\"')
        self.subrules = subrules

    def __repr__(self):
        return '{"text":"' + self.text + '","subRules":' + str(self.subrules) + '}'


class RuleSubHeader:
    def __init__(self, text, subrules):
        self.text = text.replace('"', '\\"')
        self.subrules = subrules

    def __repr__(self):
        return '{"text":"' + self.text + '","subRules":' + str(self.subrules) + '}'


class Rule:
    def __init__(self, text):
        self.text = text.replace('"', '\\"')

    def __repr__(self):
        return '{"text":"' + self.text + '"}'
