import requests
from simple_io import write
from simple_io import clear
from contract.cards import Card

URL = "https://api.scryfall.com/cards/?page="


def get_rulings(uri):
    r = requests.get(url = uri)
    data = r.json()["data"]
    return [elem["comment"] for elem in data]


def parse_card_oracle(card_json):
    if "oracle_text" in card_json:
        oracle = card_json["oracle_text"]
    else:
        oracle = parse_card_oracle(card_json["card_faces"][0]) + "\n//\n" + parse_card_oracle(card_json["card_faces"][1])
    if "power" in card_json:
        oracle = oracle + "\n" + str(card_json["power"]) + "/" + str(card_json["toughness"])
    if "loyalty" in card_json:
        oracle = oracle + "\nStarting Loyalty: " + str(card_json["loyalty"])
    return oracle


def main():
    all_cards = {}
    page_number = 1

    while True:
        print("page " + str(page_number))

        r = requests.get(url = URL + str(page_number))
        data = r.json()
        page = data["data"]

        for card_json in page:
            if not card_json["lang"] == "en" or card_json["set_type"] == "token":
                continue

            card_name = card_json["name"].replace('"', "'")
            if "mana_cost" in card_json:
                mana_cost = card_json["mana_cost"]
            else:
                mana_cost = card_json["card_faces"][0]["mana_cost"]
            type_line = card_json["type_line"].replace("�", "-")
            oracle = parse_card_oracle(card_json).replace('"', "'")
            rulings = get_rulings(card_json["rulings_uri"])
            card_set = card_json["set_name"]
            print(card_name)
            if all_cards.get(card_name):
                all_cards[card_name].add_set(card_set)
            else:
                card = Card(card_name, mana_cost, type_line, oracle, rulings, card_set)
                all_cards[card_name] = card
                print(card)

        page_number += 1

        if not data["has_more"]:
            break

    output = [all_cards[k] for k in all_cards]

    clear("cards.json")
    write("cards.json", output)


if __name__ == "__main__":
    main()