from datetime import datetime, timedelta
from typing import List
import logging
import pandas as pd
import os
import shutil
import ast
import re

logger = logging.getLogger(__name__)

def add_times_to_start(start_time, time_list):
    """
    Add a list of times to a start time to get a list of datetime objects.
    Handles MM:SS and HH:MM:SS formats, skips invalid entries.

    Args:
        start_time (datetime): The starting time to which the durations will be added.
        time_list (list[str]): List of times in "MM:SS" or "HH:MM:SS" format.

    Returns:
        list[str]: List of datetime strings in "YYYY-MM-DD HH:MM:SS" format after adding the times.
    """
    new_times = []
    try:
        for mmss in time_list:
            # Ensure it's a string for splitting
            if not isinstance(mmss, str):
                mmss = str(mmss)

            parts = mmss.strip().split(":")
            if len(parts) == 2:  # MM:SS
                minutes, seconds = map(int, parts)
            elif len(parts) == 3:  # HH:MM:SS
                hours, minutes, seconds = map(int, parts)
                minutes += hours * 60
            else:
                logger.warning(f"Skipping invalid time format: {mmss}")
                continue

            delta = timedelta(minutes=minutes, seconds=seconds)
            new_time = start_time + delta
            new_times.append(new_time.strftime("%Y-%m-%d %H:%M:%S"))

        return new_times

    except Exception as e:
        logger.info(f"UTILS.PY: Error adding times to start: {e}")
        return []



def convert_json_to_dataframe(data: dict) -> pd.DataFrame:
    """
    Convert a nested stock transaction JSON into a flattened pandas DataFrame.
    If there are no transactions, returns one row with metadata fields and NaN for transaction fields.
    """
    try:
        logger.info("UTILS.PY: Started Converting JSON to DataFrame")
        
        # Extract transactions
        transactions = data.get("transactions", [])

        # Metadata to broadcast to each transaction
        metadata = {
            "Recording_Name": data.get("Recording_Name"),
            "Transcription": data.get("Transcription"),
            "Mbo_status": data.get("Mbo_status"),
            "CSAT_Score": data.get("CSAT_Score"),
            "Missed_business_opportunity": data.get("Missed_business_opportunity"),
            "Agent_Performance_Score": data.get("Agent_Performance_Score"),
            "Action_item": data.get("Action_item"),
            "Positive_words": data.get("Positive_words"),
            "Negative_words": data.get("Negative_words"),
            "Translation": data.get("Translation"),
            "Summary": data.get("Summary"),
            "Agent_performance_score": data.get("Agent_performance_score"),
            "Opportunity_type": data.get("Opportunity_type"),
            "Stop_Loss": data.get("Stop_Loss"),
            "Target_Price": data.get("Target_Price"),
        }

        if not transactions:
            # No transactions → create a single row with only metadata
            df = pd.DataFrame([{**metadata}])
        else:
            # Add metadata to each transaction
            enriched_transactions = [
                {**transaction, **metadata} for transaction in transactions
            ]
            df = pd.DataFrame(enriched_transactions)

        logger.info("UTILS.PY: JSON converted to DataFrame")
        return df

    except Exception as e:
        logger.info(f"UTILS.PY: Error converting JSON to DataFrame: {e}")
        return pd.DataFrame()  # return empty DataFrame on error

def move_file_to_complete_folder(audio_file, destination_dir):
    """Move audio file to complete folder

    Args:
        audio_file (_type_): _description_
        destination_dir (_type_): _description_
    """
    try:
        logger.info(f"Started Moving {audio_file} to {destination_dir}")
        # Move audio file to complete folder
        backend_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
        destination_dir = os.path.join(backend_dir, "complete")
        # Make sure destination exists
        os.makedirs(destination_dir, exist_ok=True)
        shutil.move(audio_file, os.path.join(destination_dir, os.path.basename(audio_file)))
        
        logger.info(f"Moved {audio_file} to {destination_dir}")

    except Exception as e:
        logger.info(f"UTILS.PY: Error moving file to complete folder: {e}")


def convert_llm_response_to_dataframe(response, transcription: str, recording_name: str) -> pd.DataFrame:

    try:
        # Step 1: Convert to string if needed
        if not isinstance(response, str):
            response = str(response)

        # Step 2: Extract the transactions block
        transactions_match = re.search(r'transactions=\[(.*?)\]\s', response, re.DOTALL)
        transactions_data = []

        if transactions_match:
            transactions_raw = '[' + transactions_match.group(1).strip() + ']'

            # Clean it: replace StockTransaction( with { and ) with }
            transactions_clean = re.sub(r'StockTransaction\(', '{', transactions_raw)
            transactions_clean = transactions_clean.replace(')', '}')

            # Replace all = with : inside that cleaned string
            # But be careful: don’t break values like 'Agent did not cross-sell'
            # So do this carefully using regex
            transactions_clean = re.sub(r'(\w+)=', r'"\1":', transactions_clean)

            # Now it's a valid list of dictionaries
            transactions_data = ast.literal_eval(transactions_clean)

        # Step 3: Extract metadata (everything after the transactions list)
        metadata_str = response.split('] ', 1)[-1].strip()
        metadata_pattern = r'(\w+)=(".*?"|\'.*?\'|[^ ]+)'
        metadata_matches = re.findall(metadata_pattern, metadata_str)

        metadata_dict = {}
        for key, value in metadata_matches: 
            if isinstance(value, str) and (value.startswith('"') or value.startswith("'")):
                value = value[1:-1]
            metadata_dict[key] = value

        # Add the two provided fields
        metadata_dict["Transcription"] = transcription
        metadata_dict["Recording_Name"] = recording_name
        
        # Step 4: Attach metadata to each transaction
        for txn in transactions_data:
            txn.update(metadata_dict)

        # Step 5: Convert to DataFrame
        df = pd.DataFrame(transactions_data)
        return df

    except Exception as e:
        logger.info(f"Error parsing transactions data: {e}")
        raise

