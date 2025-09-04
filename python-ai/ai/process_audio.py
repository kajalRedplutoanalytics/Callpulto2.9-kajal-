
import pandas as pd
import os
from utils.logger import get_logger
import time
from db.config import GEMINI_API_KEY, COMPLETE_FOLDER
from ai.ai_operations import get_stock_data, transcribe_audio
from ai.database_function.default_select_queries import recording_details_select_query, product_details_select_query
from ai.database_function.fetch_data_from_table import get_data_from_table
from .get_dataframe_from_db import mark_recording_flag
from ai.database_operations import get_match_and_mismatch_df
from ai.utils import add_times_to_start, convert_llm_response_to_dataframe, move_file_to_complete_folder
from ai.database_function.prepare_query import prepare_arguments_for_inserting_data_into_table, prepare_query_for_recording_token_usage
from ai.database_function.insert_operations import insert_data_into_table
from ai.database_function.ai_database_operations import insert_data_into_aps_and_mbo
from ai.database_operations import get_match_and_mismatch_df
from ai.ai_function.ai_audio_operations import validate_audio_files
#from ai.aiconfig import llm_module

# loogging setup
logger = get_logger(__name__)

#================================================================================================================= Validate if upload directory exists
def validate_if_upload_directory_exists(audio_files):
    '''
    validate if upload directory exists
    '''
    try:
        if audio_files:
                upload_dir = os.path.dirname(audio_files[0])
                print(f"PROCESS_AUDIO.PY: Upload directory: {upload_dir}")
        else:
            print("No audio files found")
    except Exception as e:
        print(f"PROCESS_AUDIO.PY: Error when checking upload directory: {str(e)}")


#================================================================================================================== Validate if file exists

def validate_if_file_exists(audio_files):
    """validate if file exists

    Args:
        audio_files (_type_): _description_
    """
    try:
        audio_exists_count = 0
        for i, audio_file in enumerate(audio_files):
                print("PROCESS_AUDIO.PY: Processing audio file in for loop:", audio_file)

                # Check if audio file exists
                if os.path.exists(audio_file):
                    audio_exists_count += 1
                else:
                    logger.warning(f"PROCESS_AUDIO.PY: Audio file does NOT exist: {audio_file}")
                    continue  # Skip to the next iteration if the file doesn't exist
                # If the file exists, print the file name
                logger.info(f"PROCESS_AUDIO.PY: Audio file exists and will be processed: {audio_file}")
                print(f"{'='*80}\n")
                print(f"PROCESS_AUDIO.PY: File name - {audio_file}")
    
    except Exception as e:
        print(f"PROCESS_AUDIO.PY: Error when checking files: {str(e)}")



#================================================================================================================== Get the recording file

def get_the_recording_file(audio_file, recording_details_df):
    try:
        """
        Get the recording file from the list of audio files.
        This function checks if the audio files list is not empty and returns the first audio file.
        
        Args:
            audio_files (list): List of audio files to process.
        
        Returns:
            audio_file (str): Audio file path.
            recording_name_without_ext (str): Recording name without file extension.
            recording_flag (bool): Flag indicating if the recording has been processed.
            start_time (str): Start time of the recording.
        """
        # Extract recording name
        recording_name = os.path.basename(audio_file)
        # Remove file extension from recording name
        recording_name_without_ext = os.path.splitext(recording_name)[0]
        
        # Get the start time and recording flag from the recording details DataFrame
        matching_row = recording_details_df[recording_details_df['recording_name'] == recording_name_without_ext]
        
        # Get the recording flag and start time
        if not matching_row.empty:
            recording_flag = matching_row.iloc[0]['recording_flag'] # Get the recording flag
            start_time = matching_row.iloc[0]['start_time'] # Get the start time
        
        return audio_file, recording_name_without_ext, recording_flag, start_time
        
    except Exception as e:
        print(f"PROCESS_AUDIO.PY: Error when getting the recording file: {str(e)}") 


#================================================================================================================== Transcribe Audio Function
def transcribe_audio_file(audio_file, start_time, recording_name_without_ext, product_name_list): 
    """Transcribe audio file to English and process the transcription
    This function extracts the recording name from the audio file path, retrieves the start time,

    Args:
        audio_file (_type_): _path to the audio file_

    Returns:
        _type_: Returns a tuple containing the processed DataFrame and the recording name without extension.
    """
    try: 
        # Transcription of audio file
        audio_transcription, api1_token_count, total_word_count_on_apicall_1 = transcribe_audio(audio_file)
        # print(f"API TOKEN COUNT: {api1_token_count}")
        
        # Convert Transcribe text into English Text
        response, api2_token_count, total_word_count_on_apicall_2 = get_stock_data(recording_name_without_ext, audio_transcription, start_time, product_name_list)
        # print(f"API TOKEN COUNT: {api2_token_count}")

        total_token_data = [(recording_name_without_ext, api1_token_count, api2_token_count, total_word_count_on_apicall_1, total_word_count_on_apicall_2)]

        # prepare_query_for_recording_token_usage(table_name='recording_token_usage', data=total_token_data)

        # Convert response into JSON
        raw_df = convert_llm_response_to_dataframe(response, audio_transcription, recording_name_without_ext)
        logger.info("AI_OPERATIONS.PY: RAW DATA CONVERTED TO DATAFRAME")
        
        logger.info(f"AI_OPERATIONS.PY: RAW DATA: {raw_df.columns}")
        
        # Add recording name to the DataFrame
        start_time_list = add_times_to_start(start_time, raw_df["Order_placed_time"])
        parsed_datetimes = pd.to_datetime(start_time_list)
        raw_df["Order_placed_time"] = parsed_datetimes
        logger.info("AI_OPERATIONS.PY: START TIME ADDED TO ORDER PLACED TIME")
        
        return raw_df, recording_name_without_ext, total_token_data
    except Exception as e:
        print(f"PROCESS_AUDIO.PY: Error when transcribing audio: {str(e)}")
    

#================================================================================================================== Process Recording Data Function

def process_recording_data(recording_file_path, audio_files):
    # Get current time
    timestamp = time.strftime("%Y-%m-%d %H:%M:%S")
    # Initate logger
    logger.info("="*50 + "\n")
    logger.info(f"PROCESS_AUDIO.PY: Processing {len(audio_files)} audio files")

    # Verify file existence and print status
    try:
        validate_audio_files(audio_files)    # Validate audio files
        
        # Prepare the query to get data from trading_order_detail table
        recording_details_query=recording_details_select_query
        product_details_query=product_details_select_query

        # Fetch data from the database in one go
        recording_details_data, product_details_data =get_data_from_table('false', 'false', 'false', 'false',recording_details_query,product_details_query)
        logger.info("PROCESS_AUDIO.PY: Fetched data from recording_details and product_details tables")
        # Create a DataFrame from the fetched data from recording_details_data
        df_recording_details = pd.DataFrame(recording_details_data, columns=["client_id","rm_id","recording_details_id", "recording_id", "recording_name","recording_flag","start_time","end_time"])
        # Create a DataFrame from the fetched data from recording_details_data
        df_product_details = pd.DataFrame(product_details_data, columns=["product_details_id", "product_name"])
        
        
        insert_query = []  # To collect insert queries from all audio files
        # Check if upload directory exists and has write permissions
        for i, audio_file in enumerate(audio_files):
            audio_file_path = audio_file
            recording_name = os.path.basename(audio_file)
            recording_name_without_ext = os.path.splitext(recording_name)[0]
            logger.info(f"PROCESS_AUDIO.PY: Processing audio file: {recording_name_without_ext}")
            
            matching_row = df_recording_details[df_recording_details['recording_name'] == recording_name_without_ext]
            
            if not matching_row.empty:
                recording_flag = matching_row.iloc[0]['recording_flag']
                start_time = matching_row.iloc[0]['start_time']
            else:
                logger.warning(f"PROCESS_AUDIO.PY: No matching record found for {recording_name_without_ext}")
                continue

            if recording_flag is None:
                try:
                    logger.info(f"PROCESS_AUDIO.PY: ============================= Starting processing of audio file: {audio_file_path}")
                    
                    # Attempt transcription
                    raw_df, recording_name_without_ext, token_data = transcribe_audio_file(
                        audio_file=audio_file_path, 
                        start_time=start_time, 
                        recording_name_without_ext=recording_name_without_ext,
                        product_name_list=df_product_details['product_name'],
                    )
                    
                    logger.info(f"PROCESS_AUDIO.PY: ============================= File Processing Completed. {audio_file_path}")

                    if raw_df is not None:
                        logger.info("PROCESS_AUDIO.PY: Performing batch insert for processed audio file.")

                        insert_query, list_all_data = prepare_arguments_for_inserting_data_into_table(
                            raw_df, table_name='call_order_details', query=''
                        )
                        insert_data_into_table(insert_query, list_all_data, table_name='call_order_details', args_value='true')

                        insert_data_into_aps_and_mbo(
                            recording_name=str(raw_df["Recording_Name"].iloc[0]), 
                            agent_performance_score=int(raw_df["Agent_Performance_Score"].iloc[0]), 
                            csat_score=int(raw_df["CSAT_Score"].iloc[0]),
                            mbo_summary=str(raw_df["Missed_business_opportunity"].iloc[0]), 
                            opportunity_type=str(raw_df["Opportunity_type"].iloc[0]),
                            positive_words=str(raw_df["Positive_words"].iloc[0]),
                            negative_words=str(raw_df["Negative_words"].iloc[0]),
                            product_name=str(raw_df['Product_name'].iloc[0]),
                            mbo_status=str(raw_df['Mbo_status'].iloc[0]),
                            recording_details_data=df_recording_details
                        )

                        prepare_query_for_recording_token_usage(
                            table_name='recording_token_usage', data=token_data
                        )

                except Exception as e:
                    logger.error(f"PROCESS_AUDIO.PY: Error processing {recording_name_without_ext} — {e}")
                    continue  # Skip to next file

            else:
                logger.info("="*50 + "\n")
                logger.info(f"PROCESS_AUDIO.PY: Skipping file already processed")

        get_match_and_mismatch_df()

        for i, audio_file in enumerate(audio_files):
            recording_name = os.path.basename(audio_file)
            recording_name_without_ext = os.path.splitext(recording_name)[0]
            
            # print("PROCESS_AUDIO.PY: Hello from process_recording_data")
            mark_recording_flag(recording_name_without_ext)
            logger.info(f"PROCESS_AUDIO.PY: Marked {recording_name_without_ext} as processed")
    
    except Exception as e:
        print(f"PROCESS_AUDIO.PY: Error when checking files: {str(e)}")
        logger.error(f"Error checking files: {e}")
