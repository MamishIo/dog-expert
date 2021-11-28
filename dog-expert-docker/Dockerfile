FROM public.ecr.aws/amazoncorretto/amazoncorretto:11

ENV SOURCE_DATE_EPOCH=1622959566

WORKDIR /opt/dogexpert

# Install Python component
COPY python python/
RUN yum install -y python3-3.7.0 \
    && sed -i \
        's/from random import Random as _Random/import random; _Random = lambda: random.Random\("fixed-seed-20211129"\)/g' \
        /usr/lib64/python3.7/tempfile.py \
    && pip3 install -r python/requirements.txt

# Install Java service JAR
COPY java/service.jar java/service.jar

# Run our Java JAR but use the upper directory as the workdir
CMD java -jar java/service.jar
