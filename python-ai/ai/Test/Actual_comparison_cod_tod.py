import pandas as pd
import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '../')))
from database_function.instrument_name_comparison import resolve_instrument_name_from_llm
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '../../')))
from ai.database_function.default_select_queries import call_order_select_query,trading_order_select_query,equity_details_select_query,mismatch_reason_select_query
from ai.database_function.fetch_data_from_table import get_data_from_table
import pandas as pd
from datetime import datetime
import numpy as np


# Prepare the query to get data from trading_order_detail table
mismatch_threshold_query = trading_order_select_query
# Prepare the query to get data from call_order_detail table
call_order_query=call_order_select_query
# Prepare the query to get data from equity_data table
equity_details_query=equity_details_select_query
# Prepare the query to get data from mismtach_threshold table
mismatch_reason_query=mismatch_reason_select_query


# Fetch data from the database in one go
trading_order_data, call_order_data, equity_details_data, mismatch_reason_data=get_data_from_table(trading_order_select_query, call_order_query, equity_details_query, mismatch_reason_query)
print("Data from all tables fetched successfully###############")

# Create a DataFrame from the fetched data from trading_order_details
df_trading_order_details = pd.DataFrame(trading_order_data, columns=["client_id", "rm_id", "rm_name","client_name","trading_order_instrument_name","trading_order_quantity", "trading_order_price","order_execution_time","call_type","transaction_type","stop_loss","order_Status","target_price","product_details_id","price_threshold","quantity_threshold","time_threshold"])
# Create a DataFrame from the fetched data from call_order_details
df_call_order_details= pd.DataFrame(call_order_data, columns=["call_order_details_id","client_id", "rm_id", "rm_name","client_name","call_order_instrument_name","call_order_quantity", "call_order_price","order_placed_time","call_type","transaction_type","stop_loss","order_Status","target_price","product_details_id"])
# Create a DataFrame from the fetched data from equity_details
df_equity_details = pd.DataFrame(equity_details_data, columns=["instrument_id", "instrument_name", "nse_code","bse_code"])
# Create a DataFrame from the fetched data from mismatch_threshold
#df_mismatch_threshold = pd.DataFrame(mismatch_threshold_data, columns=["threshold_id", "price_threshold", "time_threshold", "quantity_threshold"])
# Create a DataFrame from the fetched data from mismatch_reason
df_mismatch_reason = pd.DataFrame(mismatch_reason_data, columns=["mismatch_reason_id", "category", "reason"])


#print(df_equity_details)
df_call_order_detail_resolved_instrument_name=resolve_instrument_name_from_llm(df_call_order_details,df_equity_details)

#print(df_call_order_detail_resolved_instrument_name)
#print(df_trading_order_details)

# For df_trading_order_details
df_trading_order_details['rn'] = (
    df_trading_order_details
    .sort_values(['rm_id', 'client_id', 'trading_order_instrument_name', 'order_execution_time'])
    .groupby(['rm_id', 'client_id', 'trading_order_instrument_name'])
    .cumcount() + 1
)

# For df_call_order_details
df_call_order_detail_resolved_instrument_name['rn'] = (
    df_call_order_detail_resolved_instrument_name
    .sort_values(['rm_id', 'client_id', 'matched_instrument_name', 'order_placed_time'])  # use order_placed_time
    .groupby(['rm_id', 'client_id', 'matched_instrument_name'])
    .cumcount() + 1
)

# Rename columns to align them for joining
df_trading_order_details_renamed = df_trading_order_details.rename(
    columns={'trading_order_instrument_name': 'matched_instrument_name'}
)

df_merged = pd.merge(
    df_call_order_detail_resolved_instrument_name,
    df_trading_order_details_renamed,
    on=['rn', 'rm_id', 'client_id', 'matched_instrument_name'],
    how='outer',
    suffixes=('_call', '_trade')
)
#price_thresh = float(df_mismatch_threshold.iloc[0]["price_threshold"])
#qty_thresh = float(df_mismatch_threshold.iloc[0]["quantity_threshold"])
#time_thresh = int(df_mismatch_threshold.iloc[0]["time_threshold"])

# Convert quantity and price columns to numeric
df_merged['call_order_quantity'] = pd.to_numeric(df_merged['call_order_quantity'], errors='coerce')
df_merged['trading_order_quantity'] = pd.to_numeric(df_merged['trading_order_quantity'], errors='coerce')
df_merged['call_order_price'] = pd.to_numeric(df_merged['call_order_price'], errors='coerce')
df_merged['trading_order_price'] = pd.to_numeric(df_merged['trading_order_price'], errors='coerce')
df_merged['quantity_threshold'] = pd.to_numeric(df_merged['quantity_threshold'], errors='coerce')
df_merged['price_threshold'] = pd.to_numeric(df_merged['price_threshold'], errors='coerce')
df_merged['time_threshold'] = pd.to_numeric(df_merged['time_threshold'], errors='coerce')


# Helper conditions
both_rm_present = (~df_merged['rm_name_call'].isna()) & (~df_merged['rm_name_trade'].isna())

# Final Display Names
df_merged['rm_name'] = np.where(df_merged['rm_name_call'].isna(), df_merged['rm_name_trade'], df_merged['rm_name_call'])
df_merged['client_name'] = np.where(df_merged['client_name_call'].isna(), df_merged['client_name_trade'], df_merged['client_name_call'])
df_merged['Instrument_Name'] = np.where(df_merged['call_order_instrument_name'].isna(), df_merged['matched_instrument_name'], df_merged['call_order_instrument_name'])
df_merged['call_type'] = np.where(df_merged['rm_name_call'].isna(), df_merged['rm_name_trade'], df_merged['rm_name_call'])

# Transaction Types
df_merged['call_order_transaction_type'] = df_merged['transaction_type_trade']
df_merged['trading_order_transaction_type'] = df_merged['transaction_type_call']

# Quantity calculations
df_merged['qty_difference'] = np.where(
    both_rm_present,
    df_merged['call_order_quantity'] - df_merged['trading_order_quantity'],
    np.nan
)
df_merged['qty_threshold'] = np.where(
    both_rm_present,
    df_merged['quantity_threshold'] * 100,
    np.nan
)

df_merged['qty_qa_status'] = np.where(
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

# Price calculations
df_merged['price_difference'] = np.where(
    both_rm_present,
    df_merged['call_order_price'] - df_merged['trading_order_price'],
    np.nan
)
df_merged['price_threshold'] = np.where(
    both_rm_present,
    df_merged['price_threshold'] * 100,
    np.nan
)

df_merged['price_qa_status'] = np.where(
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

# Time calculations
df_merged['time_difference'] = np.where(
    both_rm_present,
    (df_merged['order_execution_time'] - df_merged['order_placed_time']).dt.total_seconds(),
    np.nan
)
df_merged['time_threshold'] = np.where(
    both_rm_present,
    df_merged['time_threshold'] * 100,
    np.nan
)

df_merged['time_qa_status'] = np.where(
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

def get_final_qa_status(row):
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
        elif (row['qty_qa_status'] == 'Within threshold limit' or
              row['price_qa_status'] == 'Exceeds threshold limit' or
              row['time_qa_status'] == 'Exceeds threshold limit'):
            return 'mismatch'
        else:
            return 'match'

df_merged['final_qa_status'] = df_merged.apply(get_final_qa_status, axis=1)


desired_column_order = [
    "trading_order_details_id",
    "call_order_details_id",
    "rm_name",
    "client_name",
    "Instrument_Name",
    "call_type",
    "transaction_type",
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

if not missing_cols:
    final_df = df_merged[desired_column_order]
else:
    print("⚠️ Missing columns in final_df:", missing_cols)

# (Optional) move rn, rm_id, client_id, matched_instrument_name to the front for readability
#cols = ['rn', 'rm_id', 'client_id', 'matched_instrument_name'] + \
#       [col for col in df_merged.columns if col not in ['rn', 'rm_id', 'client_id', 'matched_instrument_name']]

# 7. Print or export the final DataFrame
with pd.ExcelWriter("order_details.xlsx") as writer:
    df_call_order_detail_resolved_instrument_name.to_excel(writer, sheet_name="Call Orders", index=False)
    df_trading_order_details_renamed.to_excel(writer, sheet_name="Trading Orders", index=False)
    df_merged.to_excel(writer, sheet_name="QA validation", index=False)

