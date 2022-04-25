package com.example.myapplication.data;

import com.example.myapplication.R;
import com.example.myapplication.data.model.ManifestItem;
import com.example.myapplication.data.model.ManifestMetadata;
import com.example.myapplication.data.model.ManifestOrganization;
import com.example.myapplication.data.model.ManifestResource;
import com.example.myapplication.data.model.ScoManifest;
import com.example.myapplication.data.model.ManifestResource.ScormType;
import com.example.myapplication.ui.scoSplashPage.ScoManifestLoadCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import android.Manifest;
import android.content.Context;
import android.util.Log;
import android.util.Xml;

import androidx.lifecycle.LiveData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *  Repository to handle the parsing of SCO Collection
 *  manifest files which will be used to launch and
 *  structure SCOs from the UI.
 *
 *  XMl parsing details found at:
 *  https://developer.android.com/training/basics/network-ops/xml
 */
public class ScoManifestRepository {

    private static volatile ScoManifestRepository instance;

    //  Manages threads used to read SCO Manifests
    //  as this process should take place away from the
    //  UI thread.
    private Executor mFileExecutor;
    private Context mContext;

    public static ScoManifestRepository getInstance(Context context, Executor fileExecutor) {
        if (instance == null) {
            instance = new ScoManifestRepository(context, fileExecutor);
        }
        return instance;
    }

    public ScoManifestRepository(Context context,
                                 Executor fileExecutor) {
        mFileExecutor = fileExecutor;
        mContext = context;
    }

    /**
     *  Parses the manifest file from the SCORM collection. Contains the necessary
     *  metadata, organizations and resources to display, structure and launch
     *  SCO content via the mobile LMS. Performs this operation on a new thread so
     *  as to not burden the UI thread.
     * @param   scoId The ID of the SCO collection whose manifest is to be parsed.
     * @throws FileNotFoundException
     */
    public void getScoManifest(String scoId, ScoManifestLoadCallback callback)
            throws FileNotFoundException {

        //  Locate the root directory of the sco. This will allow us
        //  to initialise the stream reader to extract the XML data.
        String scoRoot = mContext.getExternalFilesDir(null).getPath();
        File scoCollectionRootDir = new File(scoRoot + "/" + scoId);

        //  Does the SCO collection's directory exist on disk?
        if (!scoCollectionRootDir.exists()) {
            throw new FileNotFoundException(scoRoot + " Was Not Found.");
        }

        //  For simplicity's sake, we'll just assume that all SCO Collections
        //  store their manifest immediately below the root directory (which by
        //  rights is what should happen!).
        File scoManifestFilePath = new File(scoCollectionRootDir.toString() + "/" +
                "imsmanifest.xml");

        if (!scoManifestFilePath.exists()) {
            throw new FileNotFoundException(scoManifestFilePath + " Was Not Found.");
        }

        FileInputStream xmlStream = new FileInputStream(scoManifestFilePath);

        //  Parse the manifest file on a new thread to keep
        //  the UI free.
        mFileExecutor.execute(new Runnable() {
            @Override
            public void run() {
                XmlPullParser parser = Xml.newPullParser();
                try {
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(xmlStream, null);
                    parser.nextTag();
                    ScoManifest parsedManifest = readXMLFile(parser);
                    callback.updateLoadedManifest(parsedManifest);
                }

                catch(IOException e) {
                    String errorMessage = mContext.getString(R.string.manifest_io_failure);
                    Log.wtf(null, errorMessage + e.getMessage());
                    callback.updateLoadedManifest(null);
                }

                catch(XmlPullParserException e) {
                    String errorMessage = mContext.getString(R.string.manifest_parser_failure);
                    Log.wtf(null, errorMessage + e.getMessage());
                    callback.updateLoadedManifest(null);
                }

                finally {
                    try {
                        xmlStream.close();
                    }

                    catch (IOException e) {
                        String errorMessage = mContext.getString(R.string.manifest_io_failure);
                        Log.wtf(null, errorMessage + "Could Not Close XML Stream");
                    }
                }
            }
        });
    }

    /**
     *  Method to start the parsing of the manifest file. Starts at the top level
     *  of the manifest file and iterates over the top level tags until the three
     *  main sections are found. Specialised helped functions are then called to
     *  parse out each section's specific data.
     * @param parser
     * @return  A SCOManifest object that represents the XML manifest for the targetted
     *          SCO.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private ScoManifest readXMLFile(XmlPullParser parser) throws XmlPullParserException, IOException {
        ManifestMetadata metadata = null;
        List<ManifestOrganization> organizations = new ArrayList<ManifestOrganization>();
        List<ManifestResource> resources = new ArrayList<ManifestResource>();

        //  Instruct the parser that we're expecting the top level XML
        //  tag to be <manifest> (i.e the first element the cursor
        //  should be pointing to).
        parser.require(XmlPullParser.START_TAG, null, "manifest");

        //  Iterate over the XML until we hit the end of the file...
        while (parser.next() != XmlPullParser.END_TAG) {

            //  If the parser is currently pointed to the start of the XML
            //  file, just skip it. We do this because our first tag will
            //  always need skipping over to start the process.
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            //  Get the tag name of the element the parser is currently
            //  pointed at.
            String tagName = parser.getName();

            //  If we've hit the metadata tag, we can extract the various
            //  metadata that will provde some useful context on the UI.
            if (tagName.equals("metadata")) {
                metadata = getManifestMeta(parser);
            }

            //... If we've hit an organization tag, we'll need to parse
            //    out the structure of the learning units.
            else if (tagName.equals("organizations")) {
                organizations = getManifestOrganizations(parser);
            }

            //... If we've hit a resources tag, we'll need to parse out
            //    the SCOs and assets.
            else if (tagName.equals("resources")) {
                resources = getManifestResources(parser);
            }

            //... We've hit a tag we're not interested in. Skip it!
            else {
                skipTag(parser);
            }
        }
        return new ScoManifest(metadata, organizations, resources);
    }

    /**
     *  Method to extract the XML Manifest's Metadata. Can be used to detail
     *  the SCORM schema and version used.
     * @param parser    The XML Parser used to parse the manifest.
     * @return          A ManifestMetadata object that represents the metadata
     *                  used to describe the SCO collection.
     * @throws IOException
     * @throws XmlPullParserException
     */
    private ManifestMetadata getManifestMeta(XmlPullParser parser) throws IOException, XmlPullParserException {
        String schemaValue = "";
        String schemaVersion = "";
        parser.require(XmlPullParser.START_TAG, null, "metadata");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String metaTagName = parser.getName();

            if (metaTagName.equals("schema")) {
                parser.require(XmlPullParser.START_TAG, null, "schema");
                schemaValue = readTag(parser);
                // If after reading the contents of the schema tag, we should have moved
                // onto an end tag. If not, then the XML is malformed.
                parser.require(XmlPullParser.END_TAG, null, "schema");
            }

            else if (metaTagName.equals("schemaversion")) {
                parser.require(XmlPullParser.START_TAG, null, "schemaversion");
                schemaVersion = readTag(parser);
                parser.require(XmlPullParser.END_TAG, null, "schemaversion");
            }

            else {
                skipTag(parser);
            }
        }
        return new ManifestMetadata(schemaValue, schemaVersion);
    }

    /**
     *  Method to commence the parsing of the "organizations" section of the
     *  manifest. This section is used to present the groups and SCO structures
     *  used in the collection of SCOs. Each organization will refer to a "resource"
     *  element that instructs the LMS where the associated SCO can be launched from.
     * @param parser    The XML Parser used to parse the manifest.
     * @return          A ManifestOrganization object containing a list of all Organizations
     *                  contained within the manifest.
     * @throws IOException
     * @throws XmlPullParserException
     */
    private List<ManifestOrganization> getManifestOrganizations(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        List<ManifestOrganization> organizations = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "organizations");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String tagName = parser.getName();

            if (tagName.equals("organization")) {
                try {
                    ManifestOrganization parsedOrganization = getManifestOrganization(parser);
                    organizations.add(parsedOrganization);
                }

                catch(IOException e) {
                    Log.wtf(null, "Organization Could Not Be Parsed Due To An IO Error: "
                            + e.getMessage());
                }

                catch (XmlPullParserException e) {
                    Log.wtf(null, "Organization Could Not Be Parsed Due To A Parser " +
                            "Error: " + e.getMessage());
                }
            }

            else {
                skipTag(parser);
            }
        }
        return organizations;
    }

    /**
     *  Method to iterate over each "organization" tag in order to detail an individual
     *  unit of learning contained within the collection. Each Organization will describe
     *  the structure and order of a particular collection of SCO's.
     * @param parser    The XML Parser used to parse the manifest.
     * @return          An individual ManifestOrganization containing a set of learning items.
     * @throws IOException
     * @throws XmlPullParserException
     */
    private ManifestOrganization getManifestOrganization(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        String organizationIdentifier;
        String organizationTitle = null;
        List<ManifestItem> items = new ArrayList<>();

        //  Parse out organization identifier.
        parser.require(XmlPullParser.START_TAG, null, "organization");
        organizationIdentifier = parser.getAttributeValue(null, "identifier");

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String tagName = parser.getName();

            if (tagName.equals("title")) {
                parser.require(XmlPullParser.START_TAG, null, "title");
                String title = readTag(parser);
                parser.require(XmlPullParser.END_TAG, null, "title");
                organizationTitle = title;
            }

            else if (tagName.equals("item")) {
                try {
                    ManifestItem newItem = getManifestItem(parser);
                    items.add(newItem);
                }

                catch(IOException e) {
                    Log.wtf(null, "Item Could Not Be Parsed Due To An IO Error: "
                            + e.getMessage());
                }

                catch (XmlPullParserException e) {
                    Log.wtf(null, "Item Could Not Be Parsed Due To A Parser " +
                            "Error: " + e.getMessage());
                }
            }

            else {
                skipTag(parser);
            }
        }
        return new ManifestOrganization(organizationIdentifier, organizationTitle, items);
    }

    /**
     *  Each unit of learning is represented by an individual Item. Each item
     *  defines the files and assets it's used by linking itself to a resource.
     *  The following method extracts an individual item nested beneath an
     *  "organization" tag. An item may have additional nested children beneath it.
     *  As such, this method can be called recursively.
     * @param parser    The XML Parser used to parse the manifest.
     * @return          A parsed "item" object representing a unit of learning contained
     *                  within an organization.
     * @throws IOException
     * @throws XmlPullParserException
     */
    private ManifestItem getManifestItem(XmlPullParser parser) throws IOException,
            XmlPullParserException{
        String itemTitle = null;
        String itemParameters = null;
        String itemIdentifierRef = null;
        List<ManifestItem> childItems = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, null, "item");

        itemIdentifierRef = parser.getAttributeValue(null, "identifierref");
        itemParameters = parser.getAttributeValue(null, "parameters");
        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String tagName = parser.getName();

            if (tagName.equals("title")) {
                parser.require(XmlPullParser.START_TAG, null, "title");
                String title = readTag(parser);
                parser.require(XmlPullParser.END_TAG, null, "title");
                itemTitle = title;
            }

            else if (tagName.equals("item")) {
                ManifestItem newItem = getManifestItem(parser);
                childItems.add(newItem);
            }

            else {
                skipTag(parser);
            }
        }
        return new ManifestItem(itemTitle, itemParameters,
                itemIdentifierRef, childItems);
    }

    /**
     *  Manifest resources represent a particular unit of learning that can be
     *  launched in the LMS as part of a course or programme. Each resource
     *  tracks the files and assets used by the unit, as well as the relative filepath
     *  to the file that is responsible for launching that particular resource.
     *
     *  Resources can be defined as a "SCO" (i.e. a launchable unit of learning) or
     *  or an "asset" (i.e. supplementary files and resources used by other resources).
     *
     *  The following method parses out each resource from the manifest so that
     *  the mobile SCORM player can launch content through referencing one of the
     *  extracted Organizations.
     * @param parser    The XML Parser used to parse the manifest.
     * @return          A list of the resources parsed from the manifest.
     * @throws IOException
     * @throws XmlPullParserException
     */
    private List<ManifestResource> getManifestResources(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        List<ManifestResource> resources = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, null, "resources");

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String tagName = parser.getName();

            if (tagName.equals("resource")) {
                ManifestResource newResource = getManifestResource(parser);
                resources.add(newResource);
            }

            else {
                skipTag(parser);
            }
        }
        return resources;
    }

    /**
     *  Method to extract an individual resource from a manifest's "Resources"
     *  component.
     * @param parser    The XML Parser used to parse the manifest.
     * @return          An individual resource parsed from the manifest.
     * @throws IOException
     * @throws XmlPullParserException
     */
    private ManifestResource getManifestResource(XmlPullParser parser) throws IOException,
            XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, "resource");
        String resourceIdentifier = parser.getAttributeValue(null, "identifier");
        String resourceHref = parser.getAttributeValue(null, "href");
        String resourceTypeString = parser.getAttributeValue(null, "adlcp:scormtype");
        ScormType resourceType;

        if (resourceTypeString.equals("sco")) {
            resourceType = ScormType.SCO;
        }

        else if (resourceTypeString.equals("asset")) {
            resourceType = ScormType.ASSET;
        }

        else {
            throw new XmlPullParserException("Malformed Resource XML: ScormType Was Invalid");
        }

        //  With a resource, the inner tags are not important for our
        //  mobile LMS. Just skip over the inner tags and move onto the
        //  next "<resource>" element.
        skipTag(parser);
        return new ManifestResource(resourceIdentifier, resourceHref, resourceType);
    }

    /**
     *  If we hit a tag that we're not interested in, keep iterating over
     *  it and all of it's child nodes until we come out at the end tag.
     *
     *  If we start a skip on an end tag, something has gone wrong. We only
     *  exit the loop when the depth of the cursor hits and end tag that
     *  matches the depth of the initial tag.
     * @param parser    The XMLPullParser currently parsing the manifest file.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skipTag(XmlPullParser parser) throws XmlPullParserException, IOException {

        //  If skip has been called with the parser pointing to tag
        //  that isn't an opening tag throw an exception. It's doesn't
        //  make sense to skip over the end of a tag!
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }

        //  We need to make sure we exit the loop only when
        //  we've hit the outer most end tag (i.e. we don't want
        //  to exit the skip when we're nested inside a tag. As such,
        //  we'll track how deep the parser currently is.
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;

                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    /**
     *  Move the cursor along into the contents of the current tag.
     *  Check to see if it includes text. If it does, extract the text,
     *  move to the next tag (this should be the end tag) so the parser
     *  is ready to move on to the next child/section end tag, and return
     *  the value.
     * @param parser    the XMLPullParser currently reading the XML manifest.
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
