FROM public.ecr.aws/amazoncorretto/amazoncorretto:11

ENV SOURCE_DATE_EPOCH=1622959566

WORKDIR /opt/dogexpert

# Install Python component
COPY python python/
COPY modified_pip_install.py ./
RUN yum install -y python3-3.7.0 && python3 modified_pip_install.py

# Install Java service JAR
COPY java/service.jar java/service.jar

# Run our Java JAR but use the upper directory as the workdir
CMD java -jar java/service.jar
