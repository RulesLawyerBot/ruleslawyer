class RuleHeader:
    def __init__(self, text, subrules, keywords):
        self.text = text.replace('"', '\\"')
        self.subrules = subrules
        self.keywords = keywords

    # used to make jsons
    def __repr__(self):
        return f'{{"keywords":{self.keywords},"text":"{self.text}","subRules":{self.subrules}}}'

    # used to make jsons
    def toArray(self):
        return [[self.keywords, self.text]] + join_subrules(self.subrules)


class RuleSubHeader:
    def __init__(self, text, subrules, keywords):
        self.text = text.replace('"', '\\"')
        self.subrules = subrules
        self.keywords = keywords

    def __repr__(self):
        return f'{{"keywords":{self.keywords},"text":"{self.text}","subRules":{self.subrules}}}'

    def toArray(self):
        return [[self.keywords, "", self.text]] + join_subrules(self.subrules)


class Rule:
    def __init__(self, text, keywords):
        self.text = text.replace('"', '\\"')
        self.keywords = keywords

    def __repr__(self):
        return f'{{"keywords":{self.keywords},"text":"{self.text}"}}'

    def toArray(self):
        return [[self.keywords, "", "", self.text]]


def join_subrules(subrules):
    csv_subrule = []
    for subrule in subrules:
        csv_subrule = csv_subrule + subrule.toArray()
    return csv_subrule