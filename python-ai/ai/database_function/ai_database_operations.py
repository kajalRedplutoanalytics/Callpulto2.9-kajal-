from utils.logger import get_logger
import os, re
import psycopg2
import pandas as pd
from psycopg2 import Error
from psycopg2.extras import execute_values
from db.database import connect_to_database
from ai.database_function.prepare_query import prepare_arguments_for_fetch_data_from_table
from ai.database_function.fetch_data_from_table import get_data_from_table
from ai.database_function.insert_operations import insert_data_into_table
from .dataframe_operations import get_recording_details

logger = get_logger(__name__)

import configparser

# Load config
config = configparser.ConfigParser()
config.read('config.ini')
db_conf = config['DATABASE']

schema_name = db_conf.get('schema')

tables = {
    'recording_details': db_conf.get('recording_details_table'),
    'employee_details': db_conf.get('employee_details_table'),
    'department': db_conf.get('department_table'),
    'client_details': db_conf.get('client_details_table'),
    'location': db_conf.get('location_table'),
    'trading_order_details': db_conf.get('trading_order_details_table'),
    'equity_details': db_conf.get('equity_details_table'),
    'product': db_conf.get('product_table'),
    'csat_score': db_conf.get('csat_score_table'),
    'processing_jobs': db_conf.get('processing_jobs_table'),
    'order_records': db_conf.get('order_records_table'),
    'mismatch_threshold': db_conf.get('mismatch_threshold_table'),
    'call_order_details': db_conf.get('call_order_details_table'),
    'miss_business_opportunity': db_conf.get('miss_business_opportunity_table')
}

def insert_data_into_aps_and_mbo(recording_name, agent_performance_score, csat_score, mbo_summary, opportunity_type, positive_words ,negative_words, product_name,mbo_status, recording_details_data):
    
    try:
        logger.info("AI_DATABASE_INSERT.PY : Inserting data into csat_and_agent_score and miss_business_opportunity tables has started.")
        # print("recording_details_data:", recording_details_data)
        # query=prepare_arguments_for_fetch_data_from_table(table_name=tables['recording_details'],filter_condition=recording_name)
        # result=get_data_from_table(query)
        # print("AI_DATABASE_INSERT.PY : Fetched recording details:",result)
        # if result:
        #     # recording_id, client_id, rm_id = result 
        #     recording_id = str(result[0][0])
        #     client_id = str(result[0][1])
        #     rm_id = str(result[0][2])
        logger.info("AI_DATABASE_INSERT.PY : Fetching recording details for recording_name: %s", recording_name)
        recording_id, client_id, rm_id = get_recording_details(recording_name, recording_details_data)
        logger.info("AI_DATABASE_INSERT.PY : Recording details fetched successfully: recording_id=%s, client_id=%s, rm_id=%s", recording_id, client_id, rm_id)

        # Insert data into capital_market.csat_and_agent_score table
        csat_agent_score_query=f"""
            INSERT INTO {schema_name}.{tables['csat_score']} 
            (recording_id, client_id, rm_id, csat_score, agent_performance_score, positive_words ,negative_words)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
        """            
        values = (recording_id, client_id, rm_id, csat_score, agent_performance_score, positive_words ,negative_words)
        insert_data_into_table(insert_query=csat_agent_score_query,data=values,table_name=tables['csat_score'],args_value='true')
        logger.info("AI_DATABASE_INSERT.PY : Data inserted successfully into csat_and_agent_score table.")
        
        
        miss_business_opportunity_query=f"""
            INSERT INTO {schema_name}.{tables['miss_business_opportunity']}
            (recording_id, mbo_summary, opportunity_type, mbo_status, product_name)
            VALUES (%s,%s,%s,%s,%s)
        """
        values = (recording_id, mbo_summary, opportunity_type, mbo_status, product_name)
        
        insert_data_into_table(insert_query=miss_business_opportunity_query,data=values,table_name=tables['miss_business_opportunity'],args_value='true')
        logger.info("AI_DATABASE_INSERT.PY : Data inserted successfully into csat_and_agent_score and miss_business_opportunity tables.")

    except Exception as e:
        logger.error("AI_DATABASE_INSERT.PY : Error in inserting data into csat_and_agent_score and mbo tables: %s", e)
        # print("AI_DATABASE_INSERT.PY : Error in inserting data into csat_and_agent_score and mbo tables:", e)
