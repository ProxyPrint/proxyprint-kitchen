import requests
import random
import json

# Global data configs
N_PRINTSHOPS=8
MAX_ALLOWED_BUDGETS=5
# URL
url = "http://localhost:8080/consumer/budget"
# Files location and file selection
files_pool = "/home/daniel/Desktop/files_pool/"
file_name = str(random.randrange(1,17))+".pdf"
file_path = files_pool+file_name
# PrintShops random selection
printshops = []
printshops.append(8)
pshop_id = random.randrange(1,N_PRINTSHOPS)
for i in range (1,random.randrange(1,MAX_ALLOWED_BUDGETS+1)):
    printshops.append(pshop_id)
    pshop_id += 1
    if pshop_id == N_PRINTSHOPS:
        pshop_id=1

data={"files":{file_name:{"specs":[{"id":2,"name":"","paperSpecs":"","bindingSpecs":"","coverSpecs":"","deleted":False,"from":0,"to":0}],"pages":30,"name":file_name}},"printshops":printshops}

data = {'files': open(file_path,'rb'), 'printRequest': json.dumps(data) }

r = requests.post(url, files=data, auth=("joao","1234"))

print r.text
