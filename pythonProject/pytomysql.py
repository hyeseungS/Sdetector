import pymysql

conn, cur = None, None
data1, data2, data3, dta4 = "", "", "", ""
row = None

conn = pymysql.connect(host='13.125.28.245', user='mbit', password='sdetector2021', db='MySQL', charset='utf8')
cur = conn.cursor()
sql = "CREATE TABLE IF NOT EXISTS userTable(ID string, DATE datetime, emotion int, content string)"
cur.execute(sql)
while(True):
    data1 = input("ID")
