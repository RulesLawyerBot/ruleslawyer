from json import dumps


class Card:
    def __init__(self, card_name, mana_cost, type_line, oracle, rulings, card_set, legalities, edhrec_rank, scryfall_uri, image_url):
        self.card_name = card_name
        self.mana_cost = mana_cost
        self.type_line = type_line
        self.oracle = oracle
        self.rulings = rulings
        self.sets = [card_set]
        self.legalities = legalities
        self.edhrec_rank = edhrec_rank
        self.scryfall_uri = scryfall_uri
        self.image_url = image_url

    def __repr__(self):
        return '{"cardName":"' + self.card_name + '","manaCost":"' + self.mana_cost + '","typeLine":"' + self.type_line +\
               '","oracleText":' + dumps(self.oracle) + ',"rulings":' + dumps(self.rulings) + ',"sets":' + dumps(self.sets) +\
               ',"legalities":' + dumps(self.legalities) + ',"edhrec_rank":' + str(self.edhrec_rank) +\
               ',"scryfall_uri":"' + self.scryfall_uri + '","image_url":' + dumps(self.image_url) + '}'

    def add_set(self, card_set):
        self.sets.append(card_set)
