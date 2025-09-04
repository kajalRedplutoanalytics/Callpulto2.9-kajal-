import pandas as pd
from rapidfuzz import process, fuzz

def resolve_instrument_name_from_llm(df_call_order_details, df_equity_details):
    
    df1 = df_call_order_details.merge(df_equity_details[['instrument_name']], 
                    left_on='call_order_instrument_name', 
                    right_on='instrument_name', 
                    how='left').rename(columns={'instrument_name': 'matched_instrument_name'})

    # Step 2: match with nse_code
    df2 = df1[df1['matched_instrument_name'].isna()].drop(columns=['matched_instrument_name']).merge(
        df_equity_details[['instrument_name', 'nse_code']],
        left_on='call_order_instrument_name',
        right_on='nse_code',
        how='left'
    ).drop(columns=['nse_code']).rename(columns={'instrument_name': 'matched_instrument_name'})

    # Step 3: match with bse_code
    df3 = df2[df2['matched_instrument_name'].isna()].drop(columns=['matched_instrument_name']).merge(
        df_equity_details[['instrument_name', 'bse_code']],
        left_on='call_order_instrument_name',
        right_on='bse_code',
        how='left'
    ).drop(columns=['bse_code']).rename(columns={'instrument_name': 'matched_instrument_name'})

    # 4. Step 4: Fuzzy match using normalized strings (spaces removed, lowercase)
    # Build mapping from all fields → instrument_name
    def normalize(val):
        return str(val).lower().replace(" ", "").strip()

    # Create searchable choices (normalized)
    all_keys = {}
    for _, row in df_equity_details.iterrows():
        all_keys[normalize(row['instrument_name'])] = row['instrument_name']
        all_keys[normalize(row['nse_code'])] = row['instrument_name']
        all_keys[normalize(row['bse_code'])] = row['instrument_name']

    choices = list(all_keys.keys())

    def fuzzy_match(val, threshold=85):
        val_norm = normalize(val)
        match = process.extractOne(val_norm, choices, scorer=fuzz.token_sort_ratio)
        if match and match[1] >= threshold:
            return all_keys[match[0]]
        return pd.NA

    df4 = df3[df3['matched_instrument_name'].isna()].copy()
    df4['matched_instrument_name'] = df4['call_order_instrument_name'].apply(fuzzy_match)

    # Combine all results
    final_df = pd.concat([
        df1[df1['matched_instrument_name'].notna()],
        df2[df2['matched_instrument_name'].notna()],
        df3[df3['matched_instrument_name'].notna()],
        df4
    ]).sort_values('call_order_details_id').reset_index(drop=True)

    # Final Output
    #print(final_df)
    return final_df
    