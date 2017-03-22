DATADIR=../Data
DLINADIR=../dlina-project-fork
TEI2XMI=de.unistuttgart.ims.drama.Main.TEI2XMI

MAIN.VERSION=$(xpath -e '/project/version/text()' pom.xml 2>/dev/null)

package:
	mvn clean package

run-tg: package
	java -cp target/assembly/drama.Main.jar $TEI2XMI --input $DATADIR/tei2/tg/ --output $DATADIR/xmi/tg/ --dlinaDirectory $DLINADIR/data/zwischenformat/ --genderModel src/main/resources/gender-v2.jar  --idPrefix "tg"

commitpush:
	cd $DATADIR
	git add xmi/tg/*.xmi
	git commit -m "drama.Main ${MAIN.VERSION}"
	git push
