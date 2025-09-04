# from ai.database_function.tableconfig import schema_name 
import pandas as pd
import os
import numpy as np
import pandas as pd
import configparser
from utils.logger import get_logger
from .dataframe_operations import prepare_data_for_postgres_insert
from .insert_operations import insert_data_into_table

logger = get_logger(__name__)

# Load config
config = configparser.ConfigParser()
config.read(os.path.join(os.path.dirname(__file__), '../../config.ini'))
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
    'recording_token_usage': db_conf.get('recording_token_usage_table'),
}

def prepare_arguments_for_inserting_data_into_table(raw_df, table_name, query):
    # schema_name = "capital_market" removes as we using dynamic
    
    # 1) Prepare the insert query
    if table_name == tables['call_order_details']:
        insert_query = f"""
            INSERT INTO {schema_name}.{table_name} (
                recording_name,
                extracted_instrument_name,
                quantity,
                price,
                transaction_type,
                order_status,
                stop_loss,
                target_price,
                transcription,
                translation,
                summary,
                order_placed_time,
                action_item,
                initiated_by,
                enquiry_type
            ) VALUES %s
        """
        # 3) Validate & select
        required = [
                    "Recording_Name",
                    "Instrument_Name",
                    "Quantity",
                    "Price",
                    "Transaction_Type",
                    "Order_Status",
                    "Stop_Loss",
                    "Target_Price",
                    "Transcription",
                    "Translation",
                    "Summary",
                    "Order_placed_time",
                    "Action_item",
                    "Initiated_by",
                    "Enquiry_type"
                    ]
        logger.info(f"PREPARE_QUERY.PY : Preparing query for {table_name} with columns: {required}")
        
        data_p = prepare_data_for_postgres_insert(raw_df, required)
        # logger.info(f"PREPARE_QUERY.PY : Data prepared for insertion is {data_p}")
        
        # Check if all required columns are present in the DataFrame
        missing = set(required) - set(raw_df.columns)
        logger.info(f"PREPARE_QUERY.PY : Missing columns in DataFrame: {missing}")
        # If any required columns are missing, raise a KeyError
        if missing:
            raise KeyError(f"PREPARE_QUERY.PY : DataFrame missing columns in call_order_details: {missing}")
        
        # Select only the required columns
        df_sel = raw_df[required].copy()

        # 4) Normalize NaNs to None
        df_sel = df_sel.where(pd.notna(df_sel), None)

        logger.info(f"PREPARE_QUERY.PY : Data prepared for insertion into {table_name}")
        return insert_query, data_p


    elif table_name == tables['qa_validation_output']:
        insert_query = f"""
        INSERT INTO {schema_name}.{tables['qa_validation_output']} (
            trading_order_details_id, call_order_details_id, rm_name, client_name, instrument_name, 
            call_type_from_recording,call_type_from_trading_order, transaction_type_from_recording,transaction_type_from_trading_order,
            call_order_quantity, trading_order_quantity, 
            call_order_price, trading_order_price, order_placed_time, order_execution_time, 
            qty_difference, qty_threshold, qty_qa_status, qty_mismatch_reason_id, 
            price_difference, price_threshold, price_qa_status, price_mismatch_reason_id, time_difference,
            time_threshold, time_qa_status, time_mismatch_reason_id, final_qa_status
        ) VALUES %s;
    """
        # 3) Validate & select
        required = [
            "trading_order_details_id", "call_order_details_id", "rm_name", "client_name", "instrument_name", 
            "call_type_from_recording","call_type_from_trading_order", "transaction_type_from_recording", "transaction_type_from_trading_order",
            "call_order_quantity","trading_order_quantity", 
            "call_order_price", "trading_order_price", "order_placed_time", "order_execution_time", 
            "qty_difference", "qty_threshold", "qty_qa_status", "qty_mismatch_reason_id", 
            "price_difference","price_threshold", "price_qa_status", "price_mismatch_reason_id", "time_difference",
            "time_threshold", "time_qa_status", "time_mismatch_reason_id", "final_qa_status"
        ]

        # Prepare the query based on the table name and columns
        final_df = raw_df.replace({np.nan: None, pd.NaT: None})
        
        # Convert DataFrame to list of tuples for insertion
        final_data = final_df.values.tolist()
        
        # Check if all required columns are present in the DataFrame
        missing = set(required) - set(raw_df.columns)
        # If any required columns are missing, raise a KeyError
        if missing:
            raise KeyError(f"PREPARE_QUERY.PY : DataFrame missing columns in qa_validation_output: {missing}")
        
        return insert_query, final_data
        
    elif table_name == tables['miss_business_opportunity']:
        pass
    

def prepare_arguments_for_fetch_data_from_table(table_name,filter_condition):
    """Prepare the SQL query to fetch recording_id, client_id, and rm_id from the specified table based on the filter condition.
    Args:
        table_name (str): The name of the table to fetch data from.
        filter_condition (str): The condition to filter the records.
    Returns:
        str: The prepared SQL query.
    """
    query=""
    try:
        # Prepare the query based on the table name and filter condition
        if table_name==tables['recording_details']:
            recording_name=filter_condition
            query=f"""
                SELECT recording_id, client_id, rm_id
                FROM {schema_name}.{table_name}
                WHERE recording_name = '{recording_name}';
            """
        print("PREPARE_QUERY.PY : Query for recording_id, client_id, rm_id prepared successfully")
        # If the query is successfully prepared, return it
        return query
    except Exception as e:
        print("PREPARE_QUERY.PY : Error in preparing query for recording_id, client_id, rm_id:", e)
    

def prepare_query_for_recording_token_usage(table_name, data):
    try:
        if table_name != tables['recording_token_usage']:
            logger.error(f"Invalid table name {table_name} for recording token usage.")
            return

        insert_query_for_recording_token = f"""
            INSERT INTO {schema_name}.{tables['recording_token_usage']} (recording_name, api1_tokens, api2_tokens, api1_words, api2_words)
            VALUES %s
        """
    
        insert_data_into_table(insert_query_for_recording_token, data, table_name, args_value='true')
        logger.info("Insert query for recording token usage executed successfully.")

    except Exception as e:
        logger.error(f"Error inserting recording token usage: {e}")
    