import pandas as pd
import pymysql
from sqlalchemy import create_engine

conn = pymysql.connect(host='52.78.165.117', port=3306, user='mbit', password='sdetector2021', db='sys', charset='utf8')

curs = conn.cursor()

sql = """select * from diary"""
curs.execute(sql)

data = curs.fetchall()
#print(data)


#경로 변경
df_data = pd.DataFrame(data, columns=['ID', 'DATE', 'emotion', 'content'])
#print(df_data)
df_data.to_json('df_data.json', orient='table')
df = pd.read_json('df_data.json', orient='table')
#print(df)

conn.close()

keywords_good = open('/home/ubuntu/pythonProject/good.txt', 'r').read().split('\n')
keywords_normal = open('/home/ubuntu/pythonProject/normal.txt', 'r').read().split('\n')
keywords_bad = open('/home/ubuntu/pythonProject/bad.txt', 'r').read().split('\n')

#'DATE' 배열 저장
dfSortedDATE = df.sort_values(['DATE'])
dateArray = dfSortedDATE.get('DATE')
dateCount = dateArray.count()
#for list in dateArray: print(list)

#ID
idArray = dfSortedDATE.get('ID')
idCount = idArray.count()

listArray = []
k=0
for i in range(dateCount):
    sub = []
    for j in range(1):
        sub.append(idArray[k])
        sub.append(dateArray[k])
    k += 1
    listArray.append(sub)

#print(listArray)


data_list =[]

for i in range(1):
    k=0
    #ID 분류
    for list in listArray:
        dfFilterDate = df[df['DATE'] == dateArray[k]]
        totalCount_good = 0
        totalCount_normal = 0
        totalCount_bad = 0
        for keyword_good in keywords_good:
            dfFilterKey_good = dfFilterDate[dfFilterDate['content'].str.contains(keyword_good)]
            countKey_good = dfFilterKey_good['content'].count()
            #print(list, keyword_good, countKey_good)
            totalCount_good = totalCount_good + countKey_good
        #print("")
        for keyword_normal in keywords_normal:
            dfFilterKey_normal = dfFilterDate[dfFilterDate['content'].str.contains(keyword_normal)]
            countKey_normal = dfFilterKey_normal['content'].count()
            #print(list, keyword_normal, countKey_normal)
            totalCount_normal = totalCount_normal + countKey_normal
        #print("")
        for keyword_bad in keywords_bad:
            dfFilterKey_bad = dfFilterDate[dfFilterDate['content'].str.contains(keyword_bad)]
            countKey_bad = dfFilterKey_bad['content'].count()
            #print(list, keyword_bad, countKey_bad)
            totalCount_bad = totalCount_bad + countKey_bad
        data_list.append([listArray[k][0], listArray[k][1], totalCount_good, totalCount_normal, totalCount_bad])
        #print(listArray[k][0], listArray[k][1], 'good:', totalCount_good, 'normal:', totalCount_normal, 'bad:', totalCount_bad)
        k += 1



#DataFrame 저장
dfSave = pd.DataFrame(columns=['ID', 'DATE', 'num_good', 'num_normal', 'num_bad'], data=data_list)


dfSaveSorted = dfSave.sort_values(['DATE'])

engine = create_engine('mysql+pymysql://mbit:sdetector2021@52.78.165.117/sys')
conn = engine.connect()

dfSaveSorted.to_sql(name='diary_pd', con=engine, if_exists='replace', index='False')
