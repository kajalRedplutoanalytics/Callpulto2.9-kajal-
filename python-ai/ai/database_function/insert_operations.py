
import pandas as pd
from utils.logger import get_logger
from psycopg2 import Error
from db.database import connect_to_database
from psycopg2.extras import execute_values

# from ai.database_function.tableconfig import schema_name

logger = get_logger(__name__)

import configparser

# Load config
config = configparser.ConfigParser()
config.read('config.ini')
db_conf = config['DATABASE']

# Dynamic schema and table names
schema_name = db_conf.get('schema')
tables = {
    'csat_and_agent_score': db_conf.get('csat_score_table'),
    'miss_business_opportunity': db_conf.get('miss_business_opportunity_table'),
    'call_order_details': db_conf.get('call_order_details_table'),
    'qa_validation_output': db_conf.get('qa_validation_output_table'),
    'recording_token_usage': db_conf.get('recording_token_usage_table'),
    # Add other mappings if needed
}


#-------------------------------------------------------------------
# Function to insert data into database table
#-------------------------------------------------------------------

def insert_data_into_table(insert_query, data, table_name, args_value):

    # Connect to the database
    if args_value=='true':
        # Connect to the database
        connection = connect_to_database()
    
    # If connection is None, log an error and return
    if connection is None:
        logger.error("INSERT_OPERATIONS.PY : Failed to connect to the database.")
        return

    # Check if the connection was successful
    cursor = connection.cursor()
    
    #-------------------------------------------------------------------------- INSERT OPERATIONS FOR CSAT AND AGENT SCORE
    if table_name==tables['csat_and_agent_score'] or table_name== tables['miss_business_opportunity']:
        cursor.execute(insert_query, data)
        # Commit the changes to the database
        connection.commit()
        logger.info("INSERT_OPERATIONS.PY : %d rows inserted into %s.%s", cursor.rowcount, schema_name, table_name)

    # -------------------------------------------------------------------------- INSERT OPERATIONS FOR CALL ORDER DETAILS
    elif table_name == tables['call_order_details']:
        try:
            execute_values(cursor, insert_query, data)  # execute_values instead of executemany
            connection.commit()
            logger.info("INSERT_OPERATIONS.PY : %d rows inserted into %s.%s", cursor.rowcount, schema_name, table_name)
        except Exception as e:
            connection.rollback()
            logger.error("INSERT_OPERATIONS for call_order_details : Insert failed: %s", e)
        finally:
            cursor.close()
            connection.close()
    
    # -------------------------------------------------------------------------- INSERT OPERATIONS FOR QA VALIDATION OUTPUT
    elif table_name == tables['qa_validation_output']:
        try:
            execute_values(cursor, insert_query, data)
            connection.commit()
            logger.info("INSERT_OPERATIONS.PY : %d rows inserted into %s.%s", cursor.rowcount, schema_name, table_name)
        except Exception as e:
            connection.rollback()
            logger.error("INSERT_OPERATIONS for qa_validation_output : Insert failed: %s", e)
        finally:
            cursor.close()
            connection.close()
    
    # -------------------------------------------------------------------------- INSERT OPERATIONS FOR RECORDING TOKEN USAGE
    elif table_name == tables['recording_token_usage']:
        try:
            execute_values(cursor, insert_query, data)
            connection.commit()
            logger.info("INSERT_OPERATIONS.PY : %d rows inserted into %s.%s", cursor.rowcount, schema_name, table_name)
        except Exception as e:
            connection.rollback()
            logger.error("INSERT_OPERATIONS for recording_token_usage : Insert failed: %s", e)
        finally:
            cursor.close()
            connection.close() 
