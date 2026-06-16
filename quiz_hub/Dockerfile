FROM python:alpine3.22

WORKDIR /usr/src/quiz_hub

COPY requirements.txt ./

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD [ "python3", "-m", "main" ]