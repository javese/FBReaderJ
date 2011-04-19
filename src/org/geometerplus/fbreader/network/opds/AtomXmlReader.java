package org.geometerplus.fbreader.network.opds;

import org.geometerplus.fbreader.network.atom.ATOMAuthor;
import org.geometerplus.fbreader.network.atom.ATOMCategory;
import org.geometerplus.fbreader.network.atom.ATOMEntry;
import org.geometerplus.fbreader.network.atom.ATOMFeedMetadata;
import org.geometerplus.fbreader.network.atom.ATOMIcon;
import org.geometerplus.fbreader.network.atom.ATOMId;
import org.geometerplus.fbreader.network.atom.ATOMLink;
import org.geometerplus.fbreader.network.atom.ATOMPublished;
import org.geometerplus.fbreader.network.atom.ATOMUpdated;
import org.geometerplus.zlibrary.core.xml.ZLXMLReaderAdapter;

public class AtomXmlReader extends ZLXMLReaderAdapter{

	//protected final OPDSFeedReader myFeedReader;

	private ATOMFeedMetadata myFeed;
	private ATOMEntry myEntry;

	private ATOMAuthor myAuthor;
	private ATOMId myId;
	private ATOMLink myLink;
	private ATOMCategory myCategory;
	private ATOMUpdated myUpdated;
	private ATOMPublished myPublished;
	private DCDate myDCIssued;
	private ATOMIcon myIcon;

	// TODO
}
