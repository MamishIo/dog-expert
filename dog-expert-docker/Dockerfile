FROM public.ecr.aws/amazoncorretto/amazoncorretto:11

WORKDIR /opt/dogexpert/python
COPY requirements.txt requirements.txt
RUN yum install -y python3-3.7.0 \
    && pip3 install -t . -r requirements.txt
COPY python .

WORKDIR /opt/dogexpert/java
COPY service.jar service.jar

CMD java -jar service.jar
