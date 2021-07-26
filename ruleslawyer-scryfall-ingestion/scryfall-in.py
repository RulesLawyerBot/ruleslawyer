import json
from simple_io import write
from simple_io import clear
from contract.cards import Card

FORMATS = ["standard", "brawl", "historic", "pioneer", "modern", "legacy", "vintage", "commander", "pauper"]


def parse_card_oracle(card_json):
    if "oracle_text" in card_json:
        oracle = card_json["oracle_text"]
    else:
        oracle = parse_card_oracle(card_json["card_faces"][0]) + "\n//\n" + parse_card_oracle(
            card_json["card_faces"][1])
    if "power" in card_json:
        oracle = oracle + "\n" + str(card_json["power"]) + "/" + str(card_json["toughness"])
    if "loyalty" in card_json:
        oracle = oracle + "\nStarting Loyalty: " + str(card_json["loyalty"])
    return oracle


def get_image_urls(card_json):
    if "image_uris" in card_json:
        return [card_json["image_uris"]["large"] if "large" in card_json["image_uris"] else card_json["image_uris"][0]]
    return [get_image_urls(v)[0] for v in card_json["card_faces"]]


def main():
    all_cards = {}

    cards_file = open("default-cards.json", "r", encoding="utf-8")
    cards_raw = cards_file.read()
    raw_data = json.loads(cards_raw)

    for (count, card_json) in enumerate(raw_data):
        if not card_json["lang"] == "en" or card_json["set_type"] in ["token", "memorabilia"]:
            continue

        card_name = card_json["name"].replace('"', "'")
        card_set = card_json["set_name"]
        oracle_id = card_json["oracle_id"]
        if not card_json["prices"]["usd"] and not card_json["prices"]["usd_foil"]:  # means its not a paper product
            continue

        if all_cards.get(oracle_id):
            all_cards[oracle_id].add_set(card_set)
        else:
            if "mana_cost" in card_json:
                mana_cost = card_json["mana_cost"]
            else:
                mana_cost = card_json["card_faces"][0]["mana_cost"]
            type_line = card_json["type_line"].replace("—", "-")
            oracle = parse_card_oracle(card_json).replace('"', "'")
            legalities = [k for k in card_json["legalities"] if card_json["legalities"][k] == "legal" and k in FORMATS]
            edhrec_rank = card_json["edhrec_rank"] if "edhrec_rank" in card_json else 9999999
            scryfall_uri = card_json["uri"]
            image_url = get_image_urls(card_json)
            card = Card(card_name, mana_cost, type_line, oracle, [], card_set, legalities, edhrec_rank, scryfall_uri, image_url)
            all_cards[oracle_id] = card
            print(card)

    rulings_file = open("rulings.json", "r", encoding="utf-8")
    rulings_raw = rulings_file.read()
    rulings_data = json.loads(rulings_raw)

    clear("rulingless-cards.json")
    write("rulingless-cards.json", [all_cards[k] for k in all_cards])

    for (count, rulings_json) in enumerate(rulings_data):
        if not all_cards.get(rulings_json["oracle_id"]):
            continue
        ruling = rulings_json["comment"]
        card = all_cards[rulings_json["oracle_id"]]
        card.rulings.append(ruling)
        try:
            print(ruling)
        except:
            pass

    output = [all_cards[k] for k in all_cards]

    clear("cards.json")
    write("cards.json", output)


if __name__ == "__main__":
    main()
