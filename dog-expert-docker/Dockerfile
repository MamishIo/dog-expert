FROM public.ecr.aws/amazoncorretto/amazoncorretto:11

WORKDIR /opt/dogexpert

# Install Python component
COPY python/requirements.txt python/requirements.txt
RUN yum install -y python3-3.7.0 \
    && pip3 install -t python -r python/requirements.txt

# Install Java service JAR
COPY java/service.jar java/service.jar

# Run our Java JAR but use the upper directory as the workdir
CMD java -jar java/service.jar
