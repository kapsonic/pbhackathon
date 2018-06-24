from pymongo import MongoClient


client = MongoClient("mongodb://localhost:27017")
db = client['invoices']

for i in range(0,50000):
	
	result = db.invoiceData4.insert_one(

		{
			"customerNo":"0129",
		   "date":"20180320",
		   "salesPerson":"Andrew" + str(i+1),
		   "poNumber":387522,
		   "shipAddress":{
			  "name":"Mrs Cersie",
			  "familyName":"",
			  "streetAddress":"12 Annst",
			  "city":"Old Greenwich",
			  "country":"CT",
			  "pinCode":6870
		   },
		   "billAddress":{
			  "name":"Mrs daenerys",
			  "familyName":"",
			  "streetAddress":"12 Annst",
			  "city":"Old Greenwich",
			  "country":"CT",
			  "pinCode":6870
		   },
		   "items":[
			  {
				 "qty":2,
				 "partNo":1234,
				 "description":"Engine filter",
				 "mfgNo":12,
				 "unit":"EA",
				 "each":"25.00"
			  },
			  {
				 "qty":2,
				 "partNo":1235,
				 "description":"AC Parts",
				 "mfgNo":16,
				 "unit":"EA",
				 "each":"28.00"
			  },
			  {
				 "qty":2,
				 "partNo":123342,
				 "description":"Clean Tubes",
				 "mfgNo":17,
				 "unit":"EA",
				 "each":"21.00"
			  },
			  {
				 "qty":6,
				 "partNo":15656234,
				 "description":"Cooling Fan",
				 "mfgNo":18,
				 "unit":"EA",
				 "each":"256.00"
			  },
			  {
				 "qty":1,
				 "partNo":453434,
				 "description":"Engine Valve",
				 "mfgNo":13,
				 "unit":"EA",
				 "each":"222.00"
			  }
		   ]
		}
	)