package mgr.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "files")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_seq")
    @SequenceGenerator(name = "file_seq", sequenceName = "file_seq", allocationSize = 1)
    private Long id;


    @Column(name = "file_data")
    private byte[] data;

    @Column(name = "file_name")
    private String name;

    @Column(name = "file_type")
    private String type;


    public FileData(byte[] data, String name, String type) {
        this.data = data;
        this.name = name;
        this.type = type;
    }
}
