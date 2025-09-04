
import os, re
import psycopg2
import pandas as pd
from psycopg2 import Error
#from backend.ai.database_function.fetch_data_from_table import get_data_from_table
#from ai.database_function.tableconfig import schema_name
from database_function.default_select_queries import *
from database_function.fetch_data_from_table import get_data_from_table
from database_function.default_select_queries import call_order_select_query,equity_details_select_query,mismatch_threshold_select_query

#def dataframe_from_trading_order():
    


def dataframe_from_mismatch_threshold():
    mismatch_threshold_query=mismatch_threshold_select_query
    data=get_data_from_table(mismatch_threshold_query)
    df_mismatch_threshold = pd.DataFrame(data, columns=["threshold_id", "price_threshold", "time_threshold"])
    return(df_mismatch_threshold)


df_call_order1=dataframe_from_mismatch_threshold()
print(df_call_order1)