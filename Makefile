DATADIR=../Data/
DLINADIR=../dlina-project-fork
TEI2XMI="de.unistuttgart.ims.drama.Main.TEI2XMI"

MAIN.VERSION=$(xpath -e '/project/version/text()' pom.xml 2>/dev/null)

package:
	mvn clean package

run-es: 
	java -cp target/assembly/drama.Main.jar ${TEI2XMI} --input ${DATADIR}/tei2/es/ --output ${DATADIR}/xmi/es/ --doCleanup --idPrefix "es" --language "es" --skipNER  --readerClassname "de.unistuttgart.quadrama.io.tei.textgrid.CoreTEIUrlReader"

run-tc:
	java -cp target/assembly/drama.Main.jar ${TEI2XMI} --input ${DATADIR}/tei2/tc/ --output ${DATADIR}/xmi/tc/ --doCleanup --idPrefix "tc" --language "fr" --skipNER  --readerClassname "de.unistuttgart.quadrama.io.tei.textgrid.TheatreClassicUrlReader"

run-tg: 
	java -cp target/assembly/drama.Main.jar ${TEI2XMI} --input ${DATADIR}/tei2/tg/ --output ${DATADIR}/xmi/tg/ --dlinaDirectory ${DLINADIR}/data/zwischenformat/ --genderModel src/main/resources/gender-v2.jar  --idPrefix "tg" --doCleanup

commitpush:
	git -C ${DATADIR} add xmi/tg/*.xmi
	git -C ${DATADIR} commit -m "drama.Main ${MAIN.VERSION}"
	git -C ${DATADIR} push
