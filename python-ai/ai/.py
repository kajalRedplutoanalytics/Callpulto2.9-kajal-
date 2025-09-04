def compare_and_store_results(df_call_order, df_trading_order, df_equity_details):
    # Normalize instrument names
    df_call_order["call_order_instrument_name_clean"] = df_call_order["call_order_instrument_name"].str.lower().str.strip()
    df_equity_details["instrument_name_clean"] = df_equity_details["instrument_name"].str.lower().str.strip()
    df_equity_details["nse_code_clean"] = df_equity_details["nse_code"].str.lower().str.strip()

    match_list, mismatch_list = [], []

    for _, call_row in df_call_order.iterrows():
        extracted_name = call_row["call_order_instrument_name_clean"]
        matched_row = None
        print(f"Processing extracted instrument name: {extracted_name}")
        
        # Step 2: Match instrument_name with NSE_code
        matched_row = df_equity_details[df_equity_details["nse_code_clean"] == extracted_name]
        
        # Step 3: Match instrument_name with instrument_name of equity_details
        if matched_row.empty:
            matched_row = df_equity_details[df_equity_details["instrument_name_clean"] == extracted_name]
        
        # Step 4: Regex match extracted name in instrument_name
        if matched_row.empty:
            matched_row = df_equity_details[df_equity_details["instrument_name_clean"].str.contains(re.escape(extracted_name), na=False, regex=True)]
        
        # Step 5: Remove spaces and try matching
        if matched_row.empty:
            extracted_no_space = extracted_name.replace(" ", "")
            matched_row = df_equity_details[df_equity_details["instrument_name_clean"].str.replace(" ", "", regex=True) == extracted_no_space]
        
        # Step 6: Match individual words if extracted name has multiple keywords
        if matched_row.empty and " " in extracted_name:
            words = extracted_name.split(" ")
            for word in words:
                matched_row = df_equity_details[df_equity_details["instrument_name_clean"].str.contains(re.escape(word), na=False, regex=True)]
                if not matched_row.empty:
                    break
        
        # Step 7: Regex match extracted name in NSE_code
        if matched_row.empty:
            matched_row = df_equity_details[df_equity_details["nse_code_clean"].str.contains(re.escape(extracted_name), na=False, regex=True)]
        
        # Step 8: Remove spaces and try matching again
        if matched_row.empty:
            matched_row = df_equity_details[df_equity_details["instrument_name_clean"].str.replace(" ", "", regex=True) == extracted_no_space]
        if not matched_row.empty:
            matched_name = matched_row.iloc[0]["instrument_name"]
            print(f"Match found: {extracted_name} -> {matched_name}")
        else:
            print(f"No match found for: {extracted_name}")
        
        # Compare call order and trading order for price & quantity
        for _, trade_row in df_trading_order.iterrows():
            if matched_row.empty or trade_row["instrument_name"].lower().strip() != matched_name.lower().strip():
                continue
            
            # Calculate time difference in seconds
            time_diff = (trade_row["order_execution_time"] - call_row["order_placed_time"]).total_seconds()
            
            # Check if time difference is within threshold (180 seconds = 3 minutes)
            time_match = time_diff <= 180
            
            # Check quantity and price match
            qty_price_match = (
                trade_row["trading_order_quantity"] == call_row["call_order_quantity"] and
                abs(trade_row["trading_order_price"] - call_row["call_order_price"]) <= 0.01
            )
            
            result_entry = {
                "trading_order_details_id": trade_row["trading_order_details_id"],
                "call_order_details_id": call_row["call_order_details_id"],
                "rm_name": trade_row["rm_name"],
                "client_name": trade_row["client_name"],
                "instrument_name": matched_name,
                "call_order_quantity": call_row["call_order_quantity"],
                "trading_order_quantity": trade_row["trading_order_quantity"],
                "call_order_price": call_row["call_order_price"],
                "trading_order_price": trade_row["trading_order_price"],
                "call_type": trade_row["call_type"],
                "order_placed_time": call_row["order_placed_time"],
                "order_execution_time": trade_row["order_execution_time"],
                "time_difference_seconds": time_diff,
            }
            
            if qty_price_match and time_match:
                match_list.append(result_entry)
            else:
                # Add mismatch reason
                mismatch_reasons = []
                if not qty_price_match:
                    mismatch_reasons.append("quantity/price mismatch")
                if not time_match:
                    mismatch_reasons.append(f"time difference ({time_diff} seconds)")
                result_entry["mismatch_reason"] = ", ".join(mismatch_reasons)
                mismatch_list.append(result_entry)
                
    match_df = pd.DataFrame(match_list)
    mismatch_df = pd.DataFrame(mismatch_list)

    print(f"Total matched entries: {len(match_df)}")
    print(f"Total mismatched entries: {len(mismatch_df)}")

    return match_df, mismatch_df


#################################################

import pandas as pd
import re

def compare_and_store_results(df_call_order, df_trading_order, df_equity_details):
    # Normalize instrument names
    df_call_order["call_order_instrument_name_clean"] = df_call_order["call_order_instrument_name"].str.lower().str.strip()
    df_equity_details["instrument_name_clean"] = df_equity_details["instrument_name"].str.lower().str.strip()
    df_equity_details["nse_code_clean"] = df_equity_details["nse_code"].str.lower().str.strip()

    # Convert time strings to datetime objects
    df_call_order["order_placed_time"] = pd.to_datetime(df_call_order["order_placed_time"])
    df_trading_order["order_execution_time"] = pd.to_datetime(df_trading_order["order_execution_time"])

    match_list, mismatch_list = [], []

    for _, call_row in df_call_order.iterrows():
        extracted_name = call_row["call_order_instrument_name_clean"]
        matched_row = None
        print(f"Processing extracted instrument name: {extracted_name}")
        
        # Step 2: Match instrument_name with NSE_code
        matched_row = df_equity_details[df_equity_details["nse_code_clean"] == extracted_name]
        
        # Step 3: Match instrument_name with instrument_name of equity_details
        if matched_row.empty:
            matched_row = df_equity_details[df_equity_details["instrument_name_clean"] == extracted_name]
        
        # Step 4: Regex match extracted name in instrument_name
        if matched_row.empty:
            matched_row = df_equity_details[df_equity_details["instrument_name_clean"].str.contains(re.escape(extracted_name), na=False, regex=True)]
        
        # Step 5: Remove spaces and try matching
        if matched_row.empty:
            extracted_no_space = extracted_name.replace(" ", "")
            matched_row = df_equity_details[df_equity_details["instrument_name_clean"].str.replace(" ", "", regex=True) == extracted_no_space]
        
        # Step 6: Match individual words if extracted name has multiple keywords
        if matched_row.empty and " " in extracted_name:
            words = extracted_name.split(" ")
            for word in words:
                matched_row = df_equity_details[df_equity_details["instrument_name_clean"].str.contains(re.escape(word), na=False, regex=True)]
                if not matched_row.empty:
                    break
        
        # Step 7: Regex match extracted name in NSE_code
        if matched_row.empty:
            matched_row = df_equity_details[df_equity_details["nse_code_clean"].str.contains(re.escape(extracted_name), na=False, regex=True)]
        
        # Step 8: Remove spaces and try matching again
        if matched_row.empty:
            matched_row = df_equity_details[df_equity_details["instrument_name_clean"].str.replace(" ", "", regex=True) == extracted_no_space]
        
        if not matched_row.empty:
            matched_name = matched_row.iloc[0]["instrument_name"]
            print(f"Match found: {extracted_name} -> {matched_name}")
        else:
            print(f"No match found for: {extracted_name}")
        
        # Compare call order and trading order for price & quantity
        for _, trade_row in df_trading_order.iterrows():
            if matched_row.empty or trade_row["instrument_name"].lower().strip() != matched_name.lower().strip():
                continue
            
            # Calculate time difference as timedelta
            time_diff = trade_row["order_execution_time"] - call_row["order_placed_time"]
            time_diff_seconds = time_diff.total_seconds()
            
            # Check if time difference is within threshold (180 seconds = 3 minutes)
            time_match = time_diff_seconds <= 180
            
            # Check quantity and price match
            qty_price_match = (
                trade_row["trading_order_quantity"] == call_row["call_order_quantity"] and
                abs(trade_row["trading_order_price"] - call_row["call_order_price"]) <= 0.01
            )
            
            result_entry = {
                "trading_order_details_id": trade_row["trading_order_details_id"],
                "call_order_details_id": call_row["call_order_details_id"],
                "rm_name": trade_row["rm_name"],
                "client_name": trade_row["client_name"],
                "instrument_name": matched_name,
                "call_order_quantity": call_row["call_order_quantity"],
                "trading_order_quantity": trade_row["trading_order_quantity"],
                "call_order_price": call_row["call_order_price"],
                "trading_order_price": trade_row["trading_order_price"],
                "call_type": trade_row["call_type"],
                "order_placed_time": call_row["order_placed_time"],
                "order_execution_time": trade_row["order_execution_time"],
                "time_difference": time_diff  # timedelta object (compatible with interval in DB)
            }
            
            if qty_price_match and time_match:
                match_list.append(result_entry)
            else:
                mismatch_reasons = []
                if not qty_price_match:
                    mismatch_reasons.append("quantity/price mismatch")
                if not time_match:
                    mismatch_reasons.append(f"time difference ({int(time_diff_seconds)} seconds)")
                result_entry["mismatch_reason"] = ", ".join(mismatch_reasons)
                mismatch_list.append(result_entry)
                
    match_df = pd.DataFrame(match_list)
    mismatch_df = pd.DataFrame(mismatch_list)

    print(f"Total matched entries: {len(match_df)}")
    print(f"Total mismatched entries: {len(mismatch_df)}")

    return match_df, mismatch_df


###########################

            # Calculate time difference in seconds
            mismatch_time = (trade_row["order_execution_time"] - call_row["order_placed_time"]).total_seconds()
            
            # Check if time difference is within threshold (180 seconds = 3 minutes)
            time_match = mismatch_time <= 180

            is_match = (
                trade_row["trading_order_quantity"] == call_row["call_order_quantity"] and
                abs(trade_row["trading_order_price"] - call_row["call_order_price"]) <= 0.01  # Allow small price variance
                and mismatch_time <= 180
            )
