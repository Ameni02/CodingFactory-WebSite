package codingfactory.gestion_formation.Services

import codingfactory.gestion_formation.Entities.RessourcePedagogique
import codingfactory.gestion_formation.Repositories.FormationRepository
import codingfactory.gestion_formation.Repositories.RessourcePedagogiqueRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


@Service
class RessourcePedagogiqueService {
    @Autowired
    private val ressourcePedagogiqueRepository: RessourcePedagogiqueRepository? = null

    @Autowired
    private val formationRepository: FormationRepository? = null

    // Sauvegarder une ressource pédagogique
    fun saveRessource(ressource: RessourcePedagogique): RessourcePedagogique {
        return ressourcePedagogiqueRepository!!.save(ressource)
    }

    // Récupérer une ressource pédagogique par ID
    fun getRessourceById(id: Long): RessourcePedagogique {
        return ressourcePedagogiqueRepository!!.findById(id).orElse(null)
    }

    val allRessources: List<RessourcePedagogique>
        // Récupérer toutes les ressources pédagogiques
        get() = ressourcePedagogiqueRepository!!.findAll()

    // Upload fichier PDF et associer une ressource
    @kotlin.Throws(IOException::class)
    fun uploadFile(file: MultipartFile, titre: String?, description: String?, formationId: Long): RessourcePedagogique {
        // Le chemin où le fichier sera stocké
        val path: Path = Paths.get("uploads/" + file.originalFilename)
        Files.write(path, file.bytes)

        // Récupérer la formation
        val formation = formationRepository!!.findById(formationId).orElseThrow {
            RuntimeException(
                "Formation non trouvée"
            )
        }

        // Créer la ressource pédagogique
        val ressource = RessourcePedagogique()
        ressource.titre = titre
        ressource.description = description
        ressource.cheminFichier = path.toString()
        ressource.formation = formation

        // Sauvegarder la ressource pédagogique
        return saveRessource(ressource)
    }
}
