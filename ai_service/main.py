
from fastapi import FastAPI
from pydantic import BaseModel
import torch
from transformers import pipeline

app = FastAPI()

# 모델 로드는 애플리케이션 시작 시 한 번만 수행
pipe = pipeline("text-generation", model="kakaocorp/kanana-1.5-2.1b-instruct-2505", torch_dtype="auto", device_map="auto", load_in_8bit=True)

class MatchData(BaseModel):
    match_info: str

@app.post("/analyze")
def analyze_match(data: MatchData):
    prompt = f"""### System:
                당신은 PUBG 게임 전문가입니다. 주어진 경기 데이터를 바탕으로 플레이어의 플레이 스타일, 강점, 그리고 개선점을 분석해주세요.

                ### User:
                {data.match_info}

                ### Assistant:
                """
    outputs = pipe(prompt, max_new_tokens=512, do_sample=True, temperature=0.7, top_p=0.9)
    analysis_result = outputs[0]["generated_text"]

    # Assistant의 답변 부분만 추출
    return {"analysis": analysis_result.split("### Assistant:")[-1].strip()}

# 로컬 테스트용: uvicorn main:app --reload
