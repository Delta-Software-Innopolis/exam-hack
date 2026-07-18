from fastapi import APIRouter, status, HTTPException, Header, Depends, Body
from config import ALGORITH, SECRET_KEY_ACCESS
import jwt
from typing import Optional, Any
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
oauth2_scheme = HTTPBearer()

def validate_token(token: HTTPAuthorizationCredentials = Depends(oauth2_scheme))-> dict[str, Any]:
    try:
        token = token.credentials #type: ignore
        payload = jwt.decode(token, SECRET_KEY_ACCESS, algorithms=[ALGORITH]) #type: ignore
        return payload
    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Access token expired")
    except (jwt.PyJWTError, jwt.DecodeError):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid access token")

def validate_pack_score(score: int = Body(embed=True)) -> None:
    if score not in [1, 2, 3, 4, 5]:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="score should be on of [1, 2, 3, 4, 5]")