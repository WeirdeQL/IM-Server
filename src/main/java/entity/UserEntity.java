package entity;

/**
 * Weirdo
 * Created on 2020-05-31 13:06
 */
public class UserEntity {
    //端口号
    private String port;
    //名称
    private String name;

    @Override
    public String toString() {
        return "UserEntity{" +
                "port='" + port + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public UserEntity() {
    }

    public UserEntity(String port, String name) {
        this.port = port;
        this.name = name;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

