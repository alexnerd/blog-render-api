FROM --platform=linux/arm64/v8 alexnerd/payaragraal:5
ENV ARCHIVE_NAME render.war
COPY ./target/render.war ${DEPLOYMENT_DIR}
