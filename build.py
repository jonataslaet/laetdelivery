import os
import threading

def build_application(app):
    app_path = os.path.join(os.getcwd(), app)
    gradlew_path = os.path.join(app_path, "gradlew")

    print(f"Tentando compilar o aplicativo em: {app_path}")

    if not os.path.isdir(app_path):
        print(f"Erro: Diretório {app_path} não encontrado.")
        return

    if not os.path.isfile(gradlew_path):
        print(f"Erro: gradlew não encontrado em {app_path}")
        return

    try:
        # Garante permissão de execução
        os.system(f"chmod +x {gradlew_path}")
        # Executa o gradle no caminho absoluto, sem mudar de diretório
        result = os.system(f"{gradlew_path} -p {app_path} build -x test")
        if result != 0:
            print(f"Build falhou para {app}")
        else:
            print(f"Build finalizado com sucesso para {app}")
    except Exception as e:
        print(f"Erro ao compilar {app}: {e}")

def docker_compose_up():
    compose_dir = "/home/jonataslaet/Workspaces/github/laetdelivery"
    print("Subindo os containers com Docker Compose...")
    try:
        os.chdir(compose_dir)
        os.system("docker-compose up --build -d")
    except Exception as e:
        print(f"Erro ao subir containers: {e}")
    print("Pipeline finalizada!")

def build_all_applications():
    print("Iniciando build dos aplicativos...")
    services = [
        "order-service",
        "orchestrator-service",
        "product-validation-service",
        "payment-service",
        "inventory-service"
    ]

    threads = []

    for service in services:
        t = threading.Thread(target=build_application, args=(service,))
        t.start()
        threads.append(t)

    # Aguarda todos os builds terminarem
    for t in threads:
        t.join()

def remove_remaining_containers():
    print("Removendo containers existentes...")
    os.system("docker-compose down")
    containers = os.popen('docker ps -aq').read().split('\n')
    containers = [c for c in containers if c]

    if containers:
        print(f"Ainda há {len(containers)} container(es): {containers}")
        for container in containers:
            print(f"Parando container {container}")
            os.system(f"docker container stop {container}")
        os.system("docker container prune -f")

if __name__ == "__main__":
    print("Pipeline iniciada!")
    build_all_applications()
    remove_remaining_containers()
    threading.Thread(target=docker_compose_up).start()
