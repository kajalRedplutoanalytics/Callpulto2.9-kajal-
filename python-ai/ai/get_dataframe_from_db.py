"""
This module contains functions to fetch trading order, call order, equity details, and mismatch threshold data from the database,
and to compare and store matched and mismatched records in separate DataFrames.

"""

import pandas as pd
from typing import Tuple
from utils.logger import get_logger
import time
import shutil
from .database_operations import *
from datetime import datetime

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
    'match_entry': db_conf.get('match_entry_table'),
    'mismatch_entry': db_conf.get('mismatch_entry_table'),
    'processing_jobs': db_conf.get('processing_jobs_table'),
    'order_records': db_conf.get('order_records_table'),
    'mismatch_threshold': db_conf.get('mismatch_threshold_table'),
    'call_order_details': db_conf.get('call_order_details_table'),
    'miss_business_opportunity': db_conf.get('miss_business_opportunity_table')
}



def mark_recording_flag(recording_name):
    """ Marks a recording as processed by updating the recording_flag in the database.
    Args:
        recording_name (str): The name of the recording to mark as processed.   
    """
    try:
        # Get database connection
        conn = connect_to_database()
        cursor = conn.cursor()

        # Update query to set recording_flag = 'Y' for the given recording name
        query = f"""
        UPDATE {schema_name}.{tables['recording_details']}
        SET recording_flag = 'Y'
        WHERE recording_name = %s;
        """
        
        cursor.execute(query, (recording_name,)) # execute the query
        conn.commit() # commit the transaction

        # Check if any row was updated
        if cursor.rowcount > 0:
            print(f"DATABASE_OPERATION.PY:================================================== Recording flag updated for: {recording_name}")
        else:
            print(f"DATABASE_OPERATION.PY: No matching recording found for: {recording_name}")

    except Exception as e:
        print(f"DATABASE_OPERATION.PY : Mark Recording Flag function Error: {e}")
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

