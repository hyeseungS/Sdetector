import pandas as pd

df = pd.read_json("./example1.json")
print(df.count())