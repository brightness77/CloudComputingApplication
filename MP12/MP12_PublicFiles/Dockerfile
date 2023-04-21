FROM python:3.6

RUN pip --no-cache-dir install numpy==1.14.3 torch==1.4.0 torchvision==0.5.0

WORKDIR /
COPY . .

ENTRYPOINT ["python", "classify.py"]