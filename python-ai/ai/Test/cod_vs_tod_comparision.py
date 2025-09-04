import pandas as pd
from datetime import datetime

# --- Step 1: Create the COD DataFrame ---
data_cod = [
    [100, 10, "Ankanee", 1, "Harshit", "infosys", 100, 2000, "Inbound", "buy", 0, "Executed", 0, "7/9/25 10:00 AM"],
    [101, 10, "Ankanee", 1, "Harshit", "reliance", 200, 1500, "Inbound", "sell", 0, "Executed", 0, "7/9/25 11:00 AM"],
    [102, 10, "Ankanee", 2, "Kishan", "infosys", 200, 2000, "Inbound", "buy", 0, "Executed", 0, "7/9/25 1:00 PM"],
    [103, 20, "Aishwarya", 3, "Supriya", "itc", 100, 300, "outbund", "buy", 0, "Executed", 0, "7/9/25 1:00 PM"],
    [104, 20, "Aishwarya", 6, "Neelam", "godgrej propoerty", 400, 350, "outbund", "buy", 0, "Executed", 0, "7/9/25 2:00 PM"],
    [105, 20, "Aishwarya", 5, "Neeraj", "wipro", 100, 200, "outbund", "buy", 0, "Executed", 0, "7/9/25 12:00 PM"],
    [106, 20, "Aishwarya", 5, "Sujay", "asian paints", 10, 2500, "Inbound", "buy", 0, "Executed", 0, "7/9/25 3:00 PM"],
    [107, 20, "Darshan", 6, "Pratik", "gmr", 5, 92, "Inbound", "buy", 0, "Executed", 0, "7/9/25 9:45 PM"]
]
columns_cod = [
    "call_order_detail_id", "Rm_id", "rm_name", "client_id", "client_name", "matched_Instrument_Name",
    "call_order_quantity", "call_order_price", "call_type", "transaction_type", "stop_loss", "order_status",
    "target_price", "order_placed_time"
]
df_cod = pd.DataFrame(data_cod, columns=columns_cod)
df_cod['order_placed_time'] = pd.to_datetime(df_cod['order_placed_time'], format='%d/%m/%y %I:%M %p')


# --- Step 2: Create the TOD DataFrame ---
data_tod = [
    [200, 10, "Ankanee", 1, "Harshit", "infosys", 100, 2000, "Inbound", "buy", 0, "Executed", 0, "7/9/25 10:02 AM", "P01"],
    [201, 10, "Ankanee", 1, "Harshit", "reliance", 210, 2000, "Inbound", "sell", 0, "Executed", 0, "7/9/25 11:04 AM", "P01"],
    [202, 10, "Ankanee", 2, "Kishan", "infosys", 200, 2200, "Inbound", "buy", 0, "Executed", 0, "7/9/25 1:02 PM", "P01"],
    [203, 20, "Aishwarya", 3, "Supriya", "itc", 100, 300, "outbund", "buy", 0, "Executed", 0, "7/9/25 1:02 PM", "P01"],
    [204, 20, "Aishwarya", 4, "Nidhi", "bajaj auto", 50, 100, "outbund", "sell", 0, "Executed", 0, "7/9/25 11:48 AM", "P01"],
    [205, 20, "Aishwarya", 5, "Neeraj", "wipro", 100, 200, "outbund", "buy", 0, "Executed", 0, "7/9/25 3:00 PM", "P01"],
    [206, 20, "Aishwarya", 5, "Sujay", "asian paints", 10, 3000, "Inbound", "buy", 0, "Executed", 0, "7/9/25 3:03 PM", "P01"],
    [207, 20, "Darshan", 6, "Pratik", "gmr", 5, 92, "Inbound", "buy", 0, "Executed", 0, "7/9/25 10:45 PM", "P01"]
]
columns_tod = [
    "trading_order_detail_id", "Rm_id", "rm_name", "client_id", "client_name", "Instrument_Name",
    "trading_order_quantity", "trading_order_price", "call_type", "transaction_type", "stop_loss",
    "order_status", "target_price", "order_execution_time", "product_details_id"
]
df_tod = pd.DataFrame(data_tod, columns=columns_tod)
df_tod['order_execution_time'] = pd.to_datetime(df_tod['order_execution_time'], format='%m/%d/%y %I:%M %p')

# --- Step 3: Create the Threshold DataFrame ---
df_threshold = pd.DataFrame({
    "product_details_id": ["P01", "P02"],
    "price_threshold": [0.1, 0.2],
    "quantity_threshold": [1, 0],
    "time_threshold (sec)": [300, 300]
}).set_index("product_details_id")

# --- Step 4: Build composite keys for COD and TOD ---
df_cod["key"] = (
    df_cod["Rm_id"].astype(str) + "|" +
    df_cod["client_id"].astype(str) + "|" +
    df_cod["call_type"].str.lower() + "|" +
    df_cod["transaction_type"].str.lower() + "|" +
    df_cod["matched_Instrument_Name"].str.lower()
)
df_tod["key"] = (
    df_tod["Rm_id"].astype(str) + "|" +
    df_tod["client_id"].astype(str) + "|" +
    df_tod["call_type"].str.lower() + "|" +
    df_tod["transaction_type"].str.lower() + "|" +
    df_tod["Instrument_Name"].str.lower()
)

# --- Step 5: Full outer join on composite key ---
merged_df = pd.merge(df_cod, df_tod, on="key", how="outer", suffixes=('_cod', '_tod'))

# --- Step 6: Calculate time difference and filter based on threshold ---
merged_df["time_diff_sec"] = (merged_df["order_execution_time"] - merged_df["order_placed_time"]).abs().dt.total_seconds()
merged_df["threshold_sec"] = merged_df["product_details_id"].map(df_threshold["time_threshold (sec)"])

# Keep matches within time threshold or unmatched entries
merged_df_filtered = merged_df[
    (merged_df["time_diff_sec"].isna()) |
    (merged_df["threshold_sec"].isna()) |
    (merged_df["time_diff_sec"] <= merged_df["threshold_sec"])
]

# --- Step 7: Select final columns ---
final_df = merged_df_filtered[[
    "trading_order_detail_id", "call_order_detail_id", "rm_name_cod", "client_name_cod",
    "Instrument_Name", "call_type_cod", "transaction_type_cod",
    "call_order_quantity", "trading_order_quantity", "call_order_price", "trading_order_price",
    "order_placed_time", "order_execution_time"
]].copy()

# Rename for clarity
final_df.rename(columns={
    "rm_name_cod": "rm_name",
    "client_name_cod": "client_name",
    "call_type_cod": "call_type",
    "transaction_type_cod": "transaction_type"
}, inplace=True)

# Final sort
final_df.sort_values(by=["call_order_detail_id", "trading_order_detail_id"], inplace=True)
final_df.reset_index(drop=True, inplace=True)

# Show the final output
print(final_df)
