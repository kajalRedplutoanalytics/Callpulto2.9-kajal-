"""
Fetches data from the database and returns it as a DataFrame.
This module contains functions to fetch trading order, call order, equity details, and mismatch threshold data from the database,
and to compare and store matched and mismatched records in separate DataFrames.
"""
import os, re
import pandas as pd
import sys
from utils.logger import get_logger

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '../')))
from db.database import connect_to_database
from ai.database_function.fetch_data_from_table import get_data_from_table
from ai.database_function.insert_operations import insert_data_into_table
# from ai.database_function.tableconfig import schema_name
from ai.database_function.ai_database_operations import insert_data_into_aps_and_mbo
from ai.database_function.default_select_queries import call_order_select_query,trading_order_select_query,equity_details_select_query,mismatch_reason_select_query
from ai.database_function.instrument_name_comparison import resolve_instrument_name_from_llm
from ai.database_function.compare_match_and_mismatch_df import qa_validation_output
from ai.database_function.prepare_query import prepare_arguments_for_inserting_data_into_table

import configparser

logger = get_logger(__name__)

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
    'miss_business_opportunity': db_conf.get('miss_business_opportunity_table'),
    'qa_validation_output': db_conf.get('qa_validation_output_table'),
    'mismatch_reasons': db_conf.get('mismatch_reasons_table')   
}



def get_match_and_mismatch_df():
    # Prepare the query to get data from trading_order_detail table
    trading_order_query = trading_order_select_query
    # Prepare the query to get data from call_order_detail table
    call_order_query=call_order_select_query
    # Prepare the query to get data from equity_data table
    equity_details_query=equity_details_select_query
    # Prepare the query to get data from mismtach_threshold table
    #mismatch_threshold_query=mismatch_threshold_select_query
    # Prepare the query to get data from mismatch_reasons table
    mismatch_reason_query=mismatch_reason_select_query
    
    
    # Fetch data from the database in one go
    trading_order_data, call_order_data, equity_details_data, mismatch_reason_data=get_data_from_table(trading_order_query, call_order_query, equity_details_query, mismatch_reason_query,'false','false')
    logger.info("DATABASE_OPERATIONS.PY: Data fetched from the database successfully.")
    
    # Create a DataFrame from the fetched data from trading_order_details
    df_trading_order_details = pd.DataFrame(trading_order_data, columns=["trading_order_details_id","client_id", "rm_id", "rm_name","client_name","trading_order_instrument_name","trading_order_quantity", "trading_order_price","order_execution_time","call_type","transaction_type","stop_loss","order_Status","target_price","product_details_id","price_threshold","quantity_threshold","time_threshold"])
    logger.info("DATABASE_OPERATIONS.PY: DataFrame for trading order details created successfully.")

    # Create a DataFrame from the fetched data from call_order_details
    df_call_order_details= pd.DataFrame(call_order_data, columns=["call_order_details_id","client_id", "rm_id", "rm_name","client_name","call_order_instrument_name","call_order_quantity", "call_order_price","order_placed_time","call_type","transaction_type","stop_loss","order_Status","target_price","product_details_id"])
    logger.info("DATABASE_OPERATIONS.PY: DataFrame for call order details created successfully.")

    # Create a DataFrame from the fetched data from equity_details
    df_equity_details = pd.DataFrame(equity_details_data, columns=["instrument_id", "instrument_name", "nse_code","bse_code"])
    logger.info("DATABASE_OPERATIONS.PY: DataFrame for equity details created successfully.")

    # Create a DataFrame from the fetched data from mismatch_threshold
    #df_mismatch_threshold = pd.DataFrame(mismatch_threshold_data, columns=["threshold_id", "price_threshold", "time_threshold", "quantity_threshold"])
    
    # Create a DataFrame from the fetched data from mismatch_reason
    df_mismatch_reason = pd.DataFrame(mismatch_reason_data, columns=["mismatch_reason_id", "category", "reason"])
    logger.info("DATABASE_OPERATIONS.PY: DataFrame for mismatch reasons created successfully.")

    # Steps 1 to Step 8 to resolve instrument name coming from LLM
    df_call_order_detail_resolved_instrument_name=resolve_instrument_name_from_llm(df_call_order_details,df_equity_details)
    logger.info("DATABASE_OPERATIONS.PY: Instrument names resolved from LLM successfully.")

    # Compare the trading order and call order details
    df_combined=qa_validation_output(df_trading_order_details,df_call_order_detail_resolved_instrument_name,df_mismatch_reason)
    logger.info("DATABASE_OPERATIONS.PY: Matched and mismatched records compared successfully.")

    # Prepare query for qa_validation_output
    insert_query_for_qa_validation_output, values_for_qa_validation_output=prepare_arguments_for_inserting_data_into_table(raw_df=df_combined, table_name='qa_validation_output', query='1')
    logger.info("DATABASE_OPERATIONS.PY: Prepared query for inserting data into qa_validation_output table successfully.")
    logger.info(f"DATABASE_OPERATIONS.PY: Insert query for qa_validation_output: {insert_query_for_qa_validation_output}")
    logger.info(f"DATABASE_OPERATIONS.PY: Values for qa_validation_output:\n {df_combined}")

    # ✅ Insert into DB
    insert_data_into_table(insert_query_for_qa_validation_output,values_for_qa_validation_output,'qa_validation_output','true')


