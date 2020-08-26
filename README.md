# teamB_bookrentalsystem


**pom.xml에서 mvn 프로젝트로 만들기**

** local running **
kafka-server-start.bat ../../config/server.properties
zookeeper-server-start.bat ../../config/zookeeper.properties

0. Docker 
  0) jar 만들 때 package로 만들어야 한다!
  1) docker login
  2) docker build --tag isy1293/teamb-user:v1 .
  3) /*docker push*/
  docker push isy1293/teamb-user:v2
  4) hub.docker.com에서 확인
  
1. IAM 셋팅
1) accessKey...... 개별 확인
2) region : us-east-2
3) registry 생성
4) registry 로그인
aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 052937454741.dkr.ecr.us-east-2.amazonaws.com/teamb-user

3. Docker image 를 registry에 push
  docker build -t teamb-user . 
  kubectl create deploy teamb-user --image=isy1293/teamb-user:v2

  docker tag teamb-user:latest 052937454741.dkr.ecr.us-east-2.amazonaws.com/teamb-user:latest
  docker push 052937454741.dkr.ecr.us-east-2.amazonaws.com/teamb-user:latest

4. eksctl로 cluster 생성
eksctl create cluster --name teamb-Cluster --version 1.15 --nodegroup-name standard-workers --node-type t3.medium --nodes 2 --nodes-min 1 --nodes-max 3
aws eks --region us-east-2 update-kubeconfig --name teamb-Cluster
kubectl config current-context
kubectl get all

Local에 ECR(Elastic Container Registry) 인증 및 토큰 설정
aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 052937454741.dkr.ecr.us-east-2.amazonaws.com/teamb-user

5. deploy
//kubectl delete services,deployments,pods --all

kubectl create deploy teamb-user --image="052937454741.dkr.ecr.us-east-2.amazonaws.com/teamb-user:latest"
//kubectl run teamb-user --image="052937454741.dkr.ecr.us-east-2.amazonaws.com/teamb-user:latest"

//kubectl logs teamb-user-749f68f97c-hml5t -f
//kubectl exec -it teamb-user-749f68f97c-hml5t -- /bin/bash

cluster 외부 노출 /*user는 8082*/
kubectl expose deploy teamb-user --type=ClusterIP --port=8082 
  

6. gateway deploy
 
1) jar만들어서 push
 1) docker login
  2) docker build --tag isy1293/gateway:v1 .
  3) /*docker push*/
  docker push isy1293/gateway:v1
  
  (docker file add)
  buildspec.yml 복사하여 각 프로젝트에 붙여넣기


Docker image 를 registry에 push
  docker build -t gateway .
  kubectl create deploy teamb-gateway --image=isy1293/gateway:v1

  docker tag gateway:latest 052937454741.dkr.ecr.us-east-2.amazonaws.com/teamb-gateway:latest
  docker push 052937454741.dkr.ecr.us-east-2.amazonaws.com/teamb-gateway:latest

gateway 올리기
  kubectl create deploy teamb-gateway --image="052937454741.dkr.ecr.us-east-2.amazonaws.com/teamb-gateway:latest"
kubectl expose deploy teamb-gateway --type=LoadBalancer --port=8080



////////////

codeBuild

cat <<EOF|kubectl apply -f -apiVersion: v1 kind: ServiceAccount metadata: name: eks-admin namespace: kube-system EOF
