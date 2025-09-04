import sys
import os
# sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '../../../')))
from db.database import connect_to_database
from ai.database_function.tableconfig import schema_name
from utils.logger import get_logger
from utils.custom_exception import CustomException


logger = get_logger(__name__)

#-------------------------------------------------------------------
# Function to Fetch data from database table
#-------------------------------------------------------------------

def get_data_from_table(trading_order_select_query, call_order_query, equity_details_query, mismatch_reason_query, recording_details_query, product_details_query):
    # Connect to the database
    connection = connect_to_database()
    recording_details_data=[]
    product_details_data=[]
    trading_order_data = []
    call_order_data = []
    equity_details_data = []
    mismatch_reason_data = []

    # Fetch the data from the respetive tables
    if connection is not None:
        if(trading_order_select_query == 'false' and call_order_query=='false' and equity_details_query=='false' and mismatch_reason_query=='false'):
            try: 
                logger.info("FETCH_DATA_FROM_TABLE.PY: Fetching recording details and product details from the database.")
                cur = connection.cursor()
                cur.execute(recording_details_query)
                recording_details_data = cur.fetchall()
                #print(recording_details_data)
                cur.execute(product_details_query)   
                product_details_data = cur.fetchall()

                logger.info(f"FETCH_DATA_FROM_TABLE.PY: Fetched {len(recording_details_data)} recording details and {len(product_details_data)} product details from the database.")
                return (recording_details_data, product_details_data)
                    

            except Exception as e:
                logger.error(f"FETCH_DATA_FROM_TABLE.PY: Failed to fetch data from database: {e}")
                raise CustomException(f"FETCH_DATA_FROM_TABLE.PY: Failed to fetch data from database: {e}")
            
            finally:    
                cur.close()
                connection.close()
        
        else:
            try: 
                cur = connection.cursor()
                cur.execute(trading_order_select_query)
                trading_order_data = cur.fetchall()
                #print(trading_order_data)
                cur.execute(call_order_query)
                call_order_data = cur.fetchall()
                #print(call_order_data)
                
                cur.execute(equity_details_query)
                equity_details_data = cur.fetchall()
                #print(equity_details_data)

                cur.execute(mismatch_reason_query)
                mismatch_reason_data = cur.fetchall()
                #print(mismatch_reason_data)
                logger.info(f"FETCH_DATA_FROM_TABLE.PY: Fetched {len(trading_order_data)} trading order details, {len(call_order_data)} call order details, {len(equity_details_data)} equity details, and {len(mismatch_reason_data)} mismatch reasons from the database.")
            
            except Exception as e:
                logger.error(f"FETCH_DATA_FROM_TABLE.PY: Failed to fetch data from database: {e}")
                raise CustomException(f"FETCH_DATA_FROM_TABLE.PY: Failed to fetch data from database: {e}")
            finally:    
                cur.close()
                connection.close()
            return (
            trading_order_data,
            call_order_data,
            equity_details_data,
            mismatch_reason_data
        )
    else:
        logger.error("FETCH_DATA_FROM_TABLE.PY: Failed to connect to the database.")
        raise CustomException("FETCH_DATA_FROM_TABLE.PY: Failed to connect to the database.")
        
        return None

