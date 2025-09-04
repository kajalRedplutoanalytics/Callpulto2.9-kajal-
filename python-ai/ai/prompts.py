from pydantic import BaseModel, Field
from typing import List, Literal
from datetime import datetime


transcribe_prompt ="""
                    You are tasked with transcription based on audio input. I will provide you with audio files in Hindi, Gujarati, Marathi, Bengali, and Kannada. Your goal is to:
                        1. Listen to the entire audio carefully.
                        2. Generate a detailed transcription of the content.
                        3. Ensure to include:
                            - Timestamps for each speaker in the format [HH:MM:SS].
                            - The name or identifier for each person speaking, where applicable.
                            - Clear differentiation between speakers to avoid confusion.
                    Format the transcription clearly in the following structure:
                    ```
                    [HH:MM:SS] Speaker 1: [Transcription of the speech]
                    [HH:MM:SS] Speaker 2: [Transcription of the speech]
                    [HH:MM:SS] Speaker 1: [Transcription of the speech]
                    ```
                    Make sure that the transcription accurately reflects the spoken words, including any pauses, laughter, or significant non-verbal cues that are relevant. 
                    Focus on clarity and detail to ensure an accurate representation of the audio content.
                    If you encounter any words or phrases that are unclear, indicate them with [unclear] in the transcription.
                    If the audio is in a language other than Hindi, Gujarati, Marathi, Bengali, or Kannada, please indicate that the transcription cannot be performed due to language limitations.

                    """



class StockTransaction(BaseModel):
    Instrument_Name: str = Field(description="List all the names of the stock that traded or have inquiries in the conversation. If no stock name then write 'N/A'.")
    Price: float = Field(description="Price of the stock traded in the conversation, if no price then write 0. Make sure it should be equal to the stock name. If no price then write 0.")
    Quantity: int = Field(description="Quantity of the stock traded in the conversation, if no quantity then write 0. Make sure it should be equal to the stock name. if no quantity then write 0.")
    # Transaction_Type: str = Field(description="Analyze which stock the customer bought or sold. Should match the stock name.")
    Initiated_by: Literal["RM", "Customer", "N/A"] = Field(description="Who initiated the deal?")
    Product_name: str = Field(description="Category of the product from the product list., if no product name then write 'N/A'.")
    Transaction_Type: Literal["Buy", "Sell", "Enquiry"] = Field(description="Type of transaction: Buy, Sell, or Enquiry. If no transaction type then write 'N/A'.")
    Order_Status: Literal["Executed", "Pending", "Not Executed"] = Field(description="Order Status of the stock traded.")
    Enquiry_type: str = Field(description="Specify 'enquiry' or 'recommendation'. If no enquiry type then write 'N/A'.")
    Order_placed_time: str = Field(description="Provide order time in MM:SS format. If not found, use start time. If no order time then write '00:00'.")


class ConversationData(BaseModel):
    """Represents all stock transactions found in a conversation."""
    transactions: List[StockTransaction] = Field(description="A list of stock buying and selling transactions. If no transaction then write 'N/A'.")
    Mbo_status: Literal["Yes", "No"] = Field(description="Did agent fail to solve the query of the customer?")
    CSAT_Score: int = Field(description="Analyze the following customer service transcript and rate the customer satisfaction on a scale of 1 to 100, where 1 means very dissatisfied and 100 means very satisfied. Consider tone, issue resolution, response time, empathy, and clarity. Only return a single number representing the Customer Satisfaction Score (CSAT), calculated based on how satisfied the customer appears. Do not explain your reasoning.")
    Missed_business_opportunity: str = Field(description="Read the Conversation deeply and find any missed business opportunity has done by agent. Write a short descreption of 20 words.")
    Agent_Performance_Score: int = Field(description="Evaluate the agent’s performance from the transcript below and assign a single score from 1 to 100. Consider all dimensions: efficiency (speed to answer, handle time, after-call work, occupancy), effectiveness (first-contact resolution, transfers/escalations, repeat contacts), customer‑centric metrics (CSAT, NPS, CES), and quality/compliance (call quality, schedule adherence), plus supplemental if present (agent effort, sales/retention). Weigh each parameter appropriately and return only the numeric score. No explanations.")
    Opportunity_type: Literal["Upsell", "Cross-sell", "Retention", "New Product Introduction", "N/A"] = Field(description="Opportunity type of the stock traded in the conversation")
    Action_item: str = Field(description="Write a Action item for the agent to do next. That can improve the perfomance of the agent and help agent to perform better.")
    Positive_words: str = Field(description="List of all translated(English) Positive words used in the conversation.") 
    Negative_words: str = Field(description="List of all translated Negative(English) words used in the conversation.")
    Stop_Loss: int = Field(description="It will be 0 everytime")
    Target_Price: int = Field(description="It will be 0 everytime")
    Translation: str = Field(description=f"Provide the English translation of the conversation. Transcription of the conversation goes here Output: English translation.")
    Summary: str = Field(description=f"""You are a summarization assistant proficient in condensing conversations. Your task is to generate a brief summary of the discussion held between an Agent and a Customer. Focus on the following key elements.
                                        1. The stock name discussed.
                                        2. The quantity of stocks mentioned.
                                        3. Any additional stocks suggested by the Agent.
                                        4. Any inquiries made by the Customer about stocks.
                                        The summary should be concise and informative, with a word count between 80 to 100 words. Ensure clarity and retention of the essential details in your response. 
                                        """)
