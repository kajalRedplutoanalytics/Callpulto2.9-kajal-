import pandas as pd
import numpy as np
from utils.logger import get_logger

logger = get_logger(__name__)

def qa_validation_output(df_trading_order_details,df_call_order_detail_resolved_instrument_name,df_mismatch_reason):
    """
        This function performs a comparison between trading order details and call order details,
        checking for mismatches in quantity, price, and time. It also applies thresholds for these mismatches.

    Args:
        df_trading_order_details (_type_): _description_
        df_call_order_detail_resolved_instrument_name (_type_): _description_
        df_mismatch_reason (_type_): _description_

    Returns:
        _type_: _description_
    """
    logger.info("STARTING MATCH AND MISMATCH COMPARISON")
    # Add rownumber as rn with logic: rownum() over (partition by rm_id,client_id,instrument_name order by order_execution_time)
    df_trading_order_details['rn'] = (
    df_trading_order_details
    .sort_values(['rm_id', 'client_id', 'trading_order_instrument_name', 'order_execution_time'])
    .groupby(['rm_id', 'client_id', 'trading_order_instrument_name'])
    .cumcount() + 1)
    logger.info("Added row number to trading order details DataFrame")

    # Add rownumber as rn with logic: rownum() over (partition by rm_id,client_id,matched_instrument_name order by orderplaced time)
    df_call_order_detail_resolved_instrument_name['rn'] = (
        df_call_order_detail_resolved_instrument_name
        .sort_values(['rm_id', 'client_id', 'matched_instrument_name', 'order_placed_time'])  
        .groupby(['rm_id', 'client_id', 'matched_instrument_name'])
        .cumcount() + 1)  
    logger.info("Added row number to call order details DataFrame")
    
    # Rename columns to align them for joining
    df_trading_order_details_renamed = df_trading_order_details.rename(
    columns={'trading_order_instrument_name': 'matched_instrument_name'}
    )  
    logger.info("Renamed trading order instrument name to matched_instrument_name for merging")
    
    df_merged = pd.merge(
    df_call_order_detail_resolved_instrument_name,
    df_trading_order_details_renamed,
    on=['rn', 'rm_id', 'client_id', 'matched_instrument_name'],
    how='left',
    suffixes=('_call', '_trade')
    )
    logger.info("Merged call order details with trading order details")

    # Convert quantity and price columns to numeric
    df_merged['call_order_quantity'] = pd.to_numeric(df_merged['call_order_quantity'], errors='coerce')
    df_merged['trading_order_quantity'] = pd.to_numeric(df_merged['trading_order_quantity'], errors='coerce')
    df_merged['call_order_price'] = pd.to_numeric(df_merged['call_order_price'], errors='coerce')
    df_merged['trading_order_price'] = pd.to_numeric(df_merged['trading_order_price'], errors='coerce')
    df_merged['quantity_threshold'] = pd.to_numeric(df_merged['quantity_threshold'], errors='coerce')
    df_merged['price_threshold'] = pd.to_numeric(df_merged['price_threshold'], errors='coerce')
    df_merged['time_threshold'] = pd.to_numeric(df_merged['time_threshold'], errors='coerce')
    logger.info("Converted quantity and price columns to numeric")


    # Helper conditions
    both_rm_present = (~df_merged['rm_name_call'].isna()) & (~df_merged['rm_name_trade'].isna())
    logger.info("Identified rows with both RM names present")

    # Final Display Names
    df_merged['rm_name'] = np.where(df_merged['rm_name_call'].isna(), df_merged['rm_name_trade'], df_merged['rm_name_call'])
    df_merged['client_name'] = np.where(df_merged['client_name_call'].isna(), df_merged['client_name_trade'], df_merged['client_name_call'])
    df_merged['instrument_name'] = np.where(df_merged['call_order_instrument_name'].isna(), df_merged['matched_instrument_name'], df_merged['call_order_instrument_name'])
    #df_merged['call_type'] = np.where(df_merged['rm_name_call'].isna(), df_merged['rm_name_trade'], df_merged['rm_name_call'])
    logger.info("Finalized RM name, client name, and instrument name for merged DataFrame")
    
    # Transaction Types
    df_merged['transaction_type_from_trading_order'] = df_merged['transaction_type_trade']
    df_merged['transaction_type_from_recording'] = df_merged['transaction_type_call']
    logger.info("Set transaction types from trading order and recording")

    # Transaction Types
    df_merged['call_type_from_trading_order'] = df_merged['call_type_trade']
    df_merged['call_type_from_recording'] = df_merged['call_type_call']
    logger.info("Set call types from trading order and recording")

    # Quantity calculations
    df_merged['qty_difference'] = np.where(
        both_rm_present,
        df_merged['call_order_quantity'] - df_merged['trading_order_quantity'],
        np.nan
    )
    logger.info("Calculated quantity difference between call order and trading order")
    
    # Thresholds
    df_merged['qty_threshold'] = np.where(
        both_rm_present,
        df_merged['quantity_threshold'],
        np.nan
    )
    logger.info("Set quantity threshold for merged DataFrame")
    
    
    df_merged['qty_qa_status_reason'] = np.where(
        ~both_rm_present,
        None,
        np.where(
            df_merged['call_order_quantity'] == df_merged['trading_order_quantity'],
            'match',
            np.where(
                abs(df_merged['call_order_quantity'] - df_merged['trading_order_quantity']) <= 
                df_merged['call_order_quantity'] * df_merged['quantity_threshold'],
                'Within threshold limit',
                'Exceeds threshold limit'
            )
        )
    )
    logger.info("Set quantity QA status reason for merged DataFrame")

    # Quantity QA Status
    df_merged['qty_qa_status'] = np.where(
            ~both_rm_present,
            None,
            np.where(
                df_merged['call_order_quantity'] == df_merged['trading_order_quantity'],
                'match',
                np.where(
                    abs(df_merged['call_order_quantity'] - df_merged['trading_order_quantity']) <= 
                    df_merged['call_order_quantity'] * df_merged['quantity_threshold'],
                    'match',
                    'mismatch'
                )
            )
        )
    logger.info("Set quantity QA status for merged DataFrame")
    
    # Price calculations
    df_merged['price_difference'] = np.where(
        both_rm_present,
        df_merged['call_order_price'] - df_merged['trading_order_price'],
        np.nan
    )
    logger.info("Calculated price difference between call order and trading order")

    # Thresholds
    df_merged['price_qa_status_reason'] = np.where(
        ~both_rm_present,
        None,
        np.where(
            df_merged['call_order_price'] == df_merged['trading_order_price'],
            'match',
            np.where(
                abs(df_merged['call_order_price'] - df_merged['trading_order_price']) <= 
                df_merged['call_order_price'] * df_merged['price_threshold'],
                'Within threshold limit',
                'Exceeds threshold limit'
            )
        )
    )
    logger.info("Set price QA status reason for merged DataFrame")

    # Price QA Status
    df_merged['price_qa_status'] = np.where(
            ~both_rm_present,
            None,
            np.where(
                df_merged['call_order_price'] == df_merged['trading_order_price'],
                'match',
                np.where(
                    abs(df_merged['call_order_price'] - df_merged['trading_order_price']) <= 
                    df_merged['call_order_price'] * df_merged['price_threshold'],
                    'match',
                    'mismatch'
                )
            )
        )
    logger.info("Set price QA status for merged DataFrame")
    
    df_merged['order_execution_time'] = pd.to_datetime(df_merged['order_execution_time'], errors='coerce')
    df_merged['order_placed_time'] = pd.to_datetime(df_merged['order_placed_time'], errors='coerce')
    logger.info("Converted order execution and placed times to datetime format")
    
    # Time calculations
    df_merged['time_difference'] = np.where(
        both_rm_present,
        (df_merged['order_execution_time'] - df_merged['order_placed_time']).dt.total_seconds(),
        np.nan
    )
    logger.info("Calculated time difference between order execution and placed times")
    
    # Thresholds
    df_merged['time_qa_status_reason'] = np.where(
        ~both_rm_present,
        None,
        np.where(
            df_merged['order_execution_time'] == df_merged['order_placed_time'],
            'match',
            np.where(
                df_merged['order_placed_time'] > df_merged['order_execution_time'],
                'Trade First, Confirm Later',
                np.where(
                    abs((df_merged['order_execution_time'] - df_merged['order_placed_time']).dt.total_seconds()) <= df_merged['time_threshold'],
                    'Within threshold limit',
                    'Exceeds threshold limit'
                )
            )
        )
    )
    logger.info("Set time QA status reason for merged DataFrame")
    
    # Time QA Status
    df_merged['time_qa_status'] = np.where(
        ~both_rm_present,
        None,
        np.where(
            df_merged['order_execution_time'] == df_merged['order_placed_time'],
            'match',
            np.where(
                df_merged['order_placed_time'] > df_merged['order_execution_time'],
                'mismatch',
                np.where(
                    abs((df_merged['order_execution_time'] - df_merged['order_placed_time']).dt.total_seconds()) <= df_merged['time_threshold'],
                    'match',
                    'mismatch'
                )
            )
        )
    )
    logger.info("Set time QA status for merged DataFrame")
    
    
    # Final QA Status
    def get_final_qa_status(row):
        """
        Get the final QA status for a row in the merged DataFrame.

        Args:
            row (pd.Series): A row from the merged DataFrame.

        Returns:
            str: The final QA status.
        """
        if pd.isna(row['rm_name_call']):
            return 'Call Record Missing'
        elif pd.isna(row['rm_name_trade']):
            return 'Trade Record Missing'
        else:
            if row['time_qa_status'] == 'Trade First, Confirm Later':
                return 'mismatch'
            elif (row['qty_qa_status'] == 'match' and
                row['price_qa_status'] == 'match' and
                row['time_qa_status'] == 'match'):
                return 'match'
            elif (row['qty_qa_status'] == 'mismatch' or
                row['price_qa_status'] == 'mismatch' or
                row['time_qa_status'] == 'mismatch'):
                
                logger.info(f"Final QA status for row {row.name}: mismatch")
                return 'mismatch'
            else:
                return 'match'

    df_merged['final_qa_status'] = df_merged.apply(get_final_qa_status, axis=1)

        # 1. Quantity mismatch reason
    df_merged['qty_mismatch_reason'] = np.where(
        df_merged['qty_qa_status_reason'] == 'Exceeds threshold limit',
        'Quantity Exceeds threshold limit',
        None
    )

    # 2. Price mismatch reason
    df_merged['price_mismatch_reason'] = np.where(
        df_merged['price_qa_status_reason'] == 'Exceeds threshold limit',
        'Price Exceeds threshold limit',
        None
    )

    # 3. Time mismatch reason
    df_merged['time_mismatch_reason'] = np.where(
        df_merged['time_qa_status_reason'] == 'Trade First, Confirm Later',
        'Trade First, Confirm Later',
        np.where(
            df_merged['time_qa_status_reason'] == 'Exceeds threshold limit',
            'Time Exceeds threshold limit',
            None
        )
    )

        # Prepare mapping DataFrames for each category
    qty_reason_map = df_mismatch_reason[df_mismatch_reason['category'] == 'Quantity'][['reason', 'mismatch_reason_id']]
    price_reason_map = df_mismatch_reason[df_mismatch_reason['category'] == 'Price'][['reason', 'mismatch_reason_id']]
    time_reason_map = df_mismatch_reason[df_mismatch_reason['category'] == 'Time'][['reason', 'mismatch_reason_id']]

    # 4. Merge for qty_mismatch_reason_id
    df_merged = df_merged.merge(qty_reason_map, how='left', left_on='qty_mismatch_reason', right_on='reason')
    df_merged.rename(columns={'mismatch_reason_id': 'qty_mismatch_reason_id'}, inplace=True)
    df_merged.drop(columns=['reason'], inplace=True)  # drop intermediate join column

    # 5. Merge for price_mismatch_reason_id
    df_merged = df_merged.merge(price_reason_map, how='left', left_on='price_mismatch_reason', right_on='reason')
    df_merged.rename(columns={'mismatch_reason_id': 'price_mismatch_reason_id'}, inplace=True)
    df_merged.drop(columns=['reason'], inplace=True)

    # 6. Merge for time_mismatch_reason_id
    df_merged = df_merged.merge(time_reason_map, how='left', left_on='time_mismatch_reason', right_on='reason')
    df_merged.rename(columns={'mismatch_reason_id': 'time_mismatch_reason_id'}, inplace=True)
    df_merged.drop(columns=['reason'], inplace=True)

    # Convert time_difference float (in seconds) to timedelta
    df_merged['time_difference'] = pd.to_timedelta(df_merged['time_difference'], unit='s')

    logger.info(f"Converted time difference to timedelta for merged DataFrame\n {df_merged}")

    desired_column_order = [
        "trading_order_details_id",
        "call_order_details_id",
        "rm_name",
        "client_name",
        "instrument_name",
        "call_type_from_recording",
        "call_type_from_trading_order",
        "transaction_type_from_recording",
        "transaction_type_from_trading_order",
        "call_order_quantity",
        "trading_order_quantity",
        "call_order_price",
        "trading_order_price",
        "order_placed_time",
        "order_execution_time",
        "qty_difference",
        "qty_threshold",
        "qty_qa_status",
        "qty_mismatch_reason_id",
        "price_difference",
        "price_threshold",
        "price_qa_status",
        "price_mismatch_reason_id",
        "time_difference",
        "time_threshold",
        "time_qa_status",
        "time_mismatch_reason_id",
        "final_qa_status"
    ]

    # Only reorder if all columns exist; otherwise show missing ones
    missing_cols = [col for col in desired_column_order if col not in df_merged.columns]

    final_df = pd.DataFrame()
    logger.info(f"MATCH AND MISMATCH COMPARISON: {final_df.columns}")
    logger.info(f"MATCH AND MISMATCH COMPARISON: {final_df.shape}")
    if not missing_cols:
        final_df = df_merged[desired_column_order]
        return final_df
    else:
        logger.warning(f"⚠️ Missing columns in final_df: {missing_cols}")
        return df_merged
        #final_df = df_merged[desired_column_order]
    
    
